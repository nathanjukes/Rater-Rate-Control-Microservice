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

    public boolean processRequest(ApiRequest apiRequest, Org org) {
        final String redisKey = getApiRequestsKey(apiRequest.getUserId().toString(), apiRequest.getApiPath());

        // Save Request

        // Key should be in the format of: requests_userId:X_api:Y
        apiProcessingRepository.saveRequest(redisKey);


        // ZADD requests_user:1_api:post:/users 1010 1
        return false;
    }

    public RateLimitResponse getApiStatus(ApiRequest apiRequest) {
        int currentLoad = getNumberOfRequestsLastMinute(apiRequest); // Number of requests in last 60 seconds
        return new RateLimitResponse(apiRequest.getApiPath(), false, currentLoad, 100);
    }

    private int getNumberOfRequestsLastMinute(ApiRequest apiRequest) {
        final String redisKey = getMinuteRequestsKey(apiRequest.getUserId().toString(), apiRequest.getApiPath());
        return apiProcessingRepository.getMinuteRequests(redisKey);
    }
}
