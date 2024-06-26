package RateControl.Services;

import RateControl.Exceptions.BadRequestException;
import RateControl.Exceptions.InternalServerException;
import RateControl.Models.ApiLimit.ApiLimitResponse;
import RateControl.Models.ApiLimit.CustomRuleType;
import RateControl.Models.ApiRequest.ApiRequest;
import RateControl.Models.ApiRequest.RateLimitResponse;
import RateControl.Models.Auth.Auth;
import RateControl.Models.Metrics.RequestMetric;
import RateControl.Repositories.ApiProcessingRepository;
import RateControl.Security.SecurityService;
import io.jsonwebtoken.lang.Strings;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static RateControl.Config.RedisFormatter.getApiRequestsKey;
import static RateControl.Config.RedisFormatter.getMinuteRequestsKey;
import static RateControl.Models.ApiLimit.CustomRuleType.basic;
import static RateControl.Models.ApiLimit.CustomRuleType.custom;

@Service
@Transactional
public class ApiProcessingService {
    private static final Logger log = LogManager.getLogger(ApiProcessingService.class);

    private final ApiProcessingRepository apiProcessingRepository;
    private final ApiKeyService apiKeyService;
    private final SecurityService securityService;
    private final MetricsService metricsService;

    @Autowired
    public ApiProcessingService(ApiProcessingRepository apiProcessingRepository, ApiKeyService apiKeyService, SecurityService securityService, MetricsService metricsService) {
        this.apiProcessingRepository = apiProcessingRepository;
        this.apiKeyService = apiKeyService;
        this.securityService = securityService;
        this.metricsService = metricsService;
    }

    public void processRequest(ApiRequest apiRequest) {
        final String redisKey = getApiRequestsKey(apiRequest.getRedisDataString(), apiRequest.getApiPath(), apiRequest.getApiKey());
        CompletableFuture.runAsync(() -> apiProcessingRepository.saveRequest(redisKey));
    }

    // withOffset used when we want to increment it by one for concurrent requests where the stored value is eventually correct
    public RateLimitResponse getApiStatus(ApiRequest apiRequest, boolean withOffset) throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> requestsAggregate = CompletableFuture.supplyAsync(() -> getNumberOfRequestsLastMinute(apiRequest));

        CompletableFuture<Integer> apiLimit = CompletableFuture.supplyAsync(() -> {
            try {
                return getMaxLimitRuleForAPI(apiRequest);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        CompletableFuture.allOf(requestsAggregate, apiLimit).get();

        int currentLoad = requestsAggregate.get();
        int maxLoad = apiLimit.get();

        if (withOffset) {
            currentLoad++;
        }

        return new RateLimitResponse(apiRequest.getApiPath(), currentLoad > maxLoad, currentLoad, maxLoad);
    }

    public void sendMetrics(ApiRequest apiRequest, boolean requestAccepted) throws InterruptedException {
        for (int a = 0; a < 5; a++) {
            if (apiRequest.isMetaDataPresent()) {
                break;
            }
            Thread.sleep(2500);
        }

        RequestMetric requestMetric = RequestMetric.from(apiRequest, requestAccepted);
        metricsService.saveMetric(requestMetric);
    }

    private int getNumberOfRequestsLastMinute(ApiRequest apiRequest) {
        final String redisKey = getMinuteRequestsKey(apiRequest.getRedisDataString(), apiRequest.getApiPath(), apiRequest.getApiKey());
        int requestCount = 0;

        try {
            requestCount = apiProcessingRepository.getMinuteRequests(redisKey);
        } catch (Exception ex) {
            // First insert not an issue
        }

        return requestCount;
    }

    private int getMaxLimitRuleForAPI(ApiRequest apiRequest) throws BadRequestException, ExecutionException, InterruptedException {
        log.info("Getting rule for api: {}", apiRequest.getApiPath());

        String serviceId = apiKeyService.getServiceIdForApiKey(apiRequest.getApiKey()); // Move to getRuleAndSave?

        if (serviceId == null || !Strings.hasText(serviceId)) {
            log.error("Service id not found for api key: {}", apiRequest.getApiKey());
            throw new BadRequestException("Service id not found");
        }

        return getRule(apiRequest, serviceId);
    }

    private int getRule(ApiRequest apiRequest, String serviceId) throws BadRequestException, ExecutionException, InterruptedException {
        // Assume customLimit in cache, if not then use baseLimit and retrieve custom limit as user will not be over it
        // as they have not requested once in the last hour to have their custom rule in the cache
        CompletableFuture<Integer> customLimit =  CompletableFuture.supplyAsync(() -> apiProcessingRepository.getCustomApiRuleFromCache(apiRequest));
        CompletableFuture<Integer> baseLimit =  CompletableFuture.supplyAsync(() -> apiProcessingRepository.getBaseApiRuleFromCache(apiRequest.getApiPath(), apiRequest.getApiKey()));

        // Move this to janitor ?
        CompletableFuture<Integer> apiLimit = CompletableFuture.supplyAsync(() -> {
            try {
                return getRuleAndSave(apiRequest, serviceId);
            } catch (BadRequestException | InternalServerException e) {
                throw new RuntimeException(e);
            }
        });

        if (customLimit.get() != -1) {
            return customLimit.get();
        } else if (baseLimit.get() != -1) {
            return baseLimit.get();
        } else {
            return apiLimit.get();
        }
    }

    private int getRuleAndSave(ApiRequest apiRequest, String serviceId) throws BadRequestException, InternalServerException {
        Auth auth = securityService.getServiceAccountAuth(serviceId);
        Optional<ApiLimitResponse> apiLimitRetrieved = apiProcessingRepository.getApiRule(apiRequest, serviceId, auth);

        if (apiLimitRetrieved.isPresent()) {
            Optional<CustomRuleType> customRuleType = apiLimitRetrieved.map(ApiLimitResponse::getCustomRuleType);
            Optional<Integer> newLimit = apiLimitRetrieved.map(ApiLimitResponse::getUseLimit);
            apiRequest.setOrgId(apiLimitRetrieved.map(ApiLimitResponse::getOrgId).get());
            apiRequest.setAppId(apiLimitRetrieved.map(ApiLimitResponse::getAppId).get());
            apiRequest.setServiceId(apiLimitRetrieved.map(ApiLimitResponse::getServiceId).get());
            apiRequest.setApiId(apiLimitRetrieved.map(ApiLimitResponse::getApiId).get());

            if (newLimit.isPresent()) {
                if (customRuleType.equals(Optional.of(custom))) {
                    apiProcessingRepository.saveCustomApiRuleToCache(apiRequest, newLimit.get());
                } else if (customRuleType.equals(Optional.of(basic))) {
                    apiProcessingRepository.saveBaseApiRuleToCache(apiRequest.getApiPath(), apiRequest.getApiKey(), newLimit.get());
                }
                return newLimit.get();
            }
        }

        throw new InternalServerException();
    }
}
