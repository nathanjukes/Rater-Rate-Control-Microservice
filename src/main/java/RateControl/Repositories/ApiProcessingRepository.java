package RateControl.Repositories;

import io.lettuce.core.SetArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Repository
public class ApiProcessingRepository {
    private final StatefulRedisConnection<String, String> redisConnection;


    @Autowired
    public ApiProcessingRepository(StatefulRedisConnection<String, String> redisConnection) {
        this.redisConnection = redisConnection;
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
        return redisConnection.sync().keys("requests_userId:*:*:*");
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
}
