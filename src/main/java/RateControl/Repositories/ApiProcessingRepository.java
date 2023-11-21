package RateControl.Repositories;

import RateControl.Clients.RaterManagementClient;
import RateControl.Config.RedisFormatter;
import RateControl.Exceptions.BadRequestException;
import RateControl.Models.ApiLimit.ApiLimitResponse;
import RateControl.Models.ApiRequest.ApiRequest;
import RateControl.Models.Auth.Auth;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static RateControl.Config.RedisFormatter.REQUESTS_KEYS_QUERY;

@Repository
public class ApiProcessingRepository {
    private final StatefulRedisConnection<String, String> redisConnection;
    private final RaterManagementClient raterManagementClient;

    @Autowired
    public ApiProcessingRepository(StatefulRedisConnection<String, String> redisConnection, RaterManagementClient raterManagementClient) {
        this.redisConnection = redisConnection;
        this.raterManagementClient = raterManagementClient;
    }

    public void saveRequest(String key) {
        final Double timestamp = Double.valueOf((Instant.now().getEpochSecond()));
        redisConnection.sync().zadd(key, timestamp, UUID.randomUUID().toString());
    }

    public int getNumberOfRequests(String key, Double lowerBound, Double upperBound) {
        return Math.toIntExact(redisConnection.sync().zcount(key, lowerBound, upperBound));
    }

    public List<String> getAllApiRequestSets() {
        // keys requests_userId:*:*:*
        return redisConnection.sync().keys(REQUESTS_KEYS_QUERY);
    }

    public void saveMinuteRequestsValue(String key, int value) {
        // key e.g. minute_requests_userId:X_api:Y_apiKey:Z
        // TTL of 30 seconds, once all requests from a user are out of date, this runs out, otherwise it keeps refreshing every second back to 30 (See aggregateRequestData())
        redisConnection.sync().set(key, String.valueOf(value), SetArgs.Builder.ex(30));
    }

    public int getMinuteRequests(String key) {
        // key e.g. minute_requests_userId:X_api:Y_apiKey:Z
        return Integer.parseInt(redisConnection.sync().get(key));
    }

    public void removeRequests(String key, Double upperBound) {
        redisConnection.sync().zremrangebyscore(key, 0, upperBound);
    }

    public Optional<ApiLimitResponse> getApiRule(ApiRequest apiRequest, String serviceId, Auth auth) throws BadRequestException {
        return raterManagementClient.getApiSearchRule(apiRequest, serviceId, auth.getToken());
    }

    public int getCustomApiRuleFromCache(ApiRequest apiRequest) {
        String key = RedisFormatter.getCustomLimitKey(apiRequest.getUserId().toString(), apiRequest.getApiPath(), apiRequest.getApiKey());
        try {
            return Integer.parseInt(redisConnection.sync().get(key));
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    public int getBaseApiRuleFromCache(String apiPath, String apiKey) {
        String key = RedisFormatter.getBaseLimitKey(apiPath, apiKey);
        try {
            return Integer.parseInt(redisConnection.sync().get(key));
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    public void saveCustomApiRuleToCache(ApiRequest apiRequest, int limit) {
        String key = RedisFormatter.getCustomLimitKey(apiRequest.getUserId().toString(), apiRequest.getApiPath(), apiRequest.getApiKey());
        redisConnection.sync().set(key, String.valueOf(limit), SetArgs.Builder.ex(216000)); // 1 Hour
    }

    public void saveBaseApiRuleToCache(String apiPath, String apiKey, int limit) {
        String key = RedisFormatter.getBaseLimitKey(apiPath, apiKey);
        redisConnection.sync().set(key, String.valueOf(limit), SetArgs.Builder.ex(36000)); // 10 Minutes (Should be refreshing more often)
    }
}
