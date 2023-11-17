package RateControl.Repositories;

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
        redisConnection.sync().set(key, String.valueOf(value));
    }

    public int getMinuteRequests(String key) {
        // key e.g. minute_requests_userId:X_api:Y_apiKey:Z
        return Integer.parseInt(redisConnection.sync().get(key));
    }
}
