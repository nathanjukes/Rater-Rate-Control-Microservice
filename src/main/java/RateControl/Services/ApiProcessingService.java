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

import java.util.concurrent.CompletableFuture;

import static RateControl.Config.RedisFormatter.getApiRequestsKey;
import static RateControl.Config.RedisFormatter.getMinuteRequestsKey;

@Service
@Transactional
public class ApiProcessingService {
    private static final Logger log = LogManager.getLogger(ApiProcessingService.class);

    private final ApiProcessingRepository apiProcessingRepository;

    @Autowired
    public ApiProcessingService(ApiProcessingRepository apiProcessingRepository) {
        this.apiProcessingRepository = apiProcessingRepository;
    }

    public void processRequest(ApiRequest apiRequest) {
        final String redisKey = getApiRequestsKey(apiRequest.getUserId().toString(), apiRequest.getApiPath(), apiRequest.getApiKey().getApiKey());

        // Key should be in the format of: requests_userId:X_api:Y_apiKey:Z
        CompletableFuture.runAsync(() -> apiProcessingRepository.saveRequest(redisKey));
    }

    // withOffset used when we want to increment it by one for concurrent requests where the stored value is eventually correct
    public RateLimitResponse getApiStatus(ApiRequest apiRequest, boolean withOffset) {
        int currentLoad = getNumberOfRequestsLastMinute(apiRequest); // Number of requests in last 60 seconds
        if (withOffset) {
            currentLoad++;
        }

        return new RateLimitResponse(apiRequest.getApiPath(), false, currentLoad, 100);
    }

    private int getNumberOfRequestsLastMinute(ApiRequest apiRequest) {
        final String redisKey = getMinuteRequestsKey(apiRequest.getUserId().toString(), apiRequest.getApiPath(), apiRequest.getApiKey().getApiKey());
        int requestCount = 0;

        try {
            requestCount = apiProcessingRepository.getMinuteRequests(redisKey);
        } catch (Exception ex) {
            // First insert not an issue
        }

        return requestCount;
    }
}
