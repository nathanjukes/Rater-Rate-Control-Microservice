package RateControl.Services;

import RateControl.Clients.RaterManagementClient;
import RateControl.Exceptions.BadRequestException;
import RateControl.Exceptions.UnauthorizedException;
import RateControl.Models.ApiKey.ApiKey;
import RateControl.Models.ApiRequest.ApiRequest;
import RateControl.Models.ApiRequest.RateLimitResponse;
import RateControl.Models.Auth.Auth;
import RateControl.Models.Org.Org;
import RateControl.Repositories.ApiKeyRepository;
import RateControl.Repositories.ApiProcessingRepository;
import RateControl.Security.SecurityService;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static RateControl.Config.RedisFormatter.getApiRequestsKey;
import static RateControl.Config.RedisFormatter.getMinuteRequestsKey;

@Service
@Transactional
public class ApiProcessingService {
    private static final Logger log = LogManager.getLogger(ApiProcessingService.class);

    private final ApiProcessingRepository apiProcessingRepository;
    private final ApiKeyService apiKeyService;

    @Autowired
    public ApiProcessingService(ApiProcessingRepository apiProcessingRepository, ApiKeyService apiKeyService) {
        this.apiProcessingRepository = apiProcessingRepository;
        this.apiKeyService = apiKeyService;
    }

    public void processRequest(ApiRequest apiRequest) {
        final String redisKey = getApiRequestsKey(apiRequest.getUserId().toString(), apiRequest.getApiPath(), apiRequest.getApiKey());

        // Key should be in the format of: requests_userId:X_api:Y_apiKey:Z
        CompletableFuture.runAsync(() -> apiProcessingRepository.saveRequest(redisKey));
    }

    // withOffset used when we want to increment it by one for concurrent requests where the stored value is eventually correct
    public RateLimitResponse getApiStatus(ApiRequest apiRequest, boolean withOffset, Auth auth) throws ExecutionException, InterruptedException {
        // Get current aggregate
        CompletableFuture<Integer> requestsAggregate = CompletableFuture.supplyAsync(() -> getNumberOfRequestsLastMinute(apiRequest)); // Number of requests in last 60 seconds

        // Get Limit for API
        CompletableFuture<Integer> apiLimit = CompletableFuture.supplyAsync(() -> {
            try {
                return getMaxLimitRuleForAPI(apiRequest, auth);
            } catch (BadRequestException e) {
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

    private int getNumberOfRequestsLastMinute(ApiRequest apiRequest) {
        final String redisKey = getMinuteRequestsKey(apiRequest.getUserId().toString(), apiRequest.getApiPath(), apiRequest.getApiKey());
        int requestCount = 0;

        try {
            requestCount = apiProcessingRepository.getMinuteRequests(redisKey);
        } catch (Exception ex) {
            // First insert not an issue
        }

        return requestCount;
    }

    private int getMaxLimitRuleForAPI(ApiRequest apiRequest, Auth auth) throws BadRequestException {
        log.info("Getting rule for api: {}", apiRequest.getApiPath());
        String serviceId = apiKeyService.getServiceIdForApiKey(apiRequest.getApiKey());
        return apiProcessingRepository.getApiRule(apiRequest, serviceId, auth);
    }
}
