package RateControl.Repositories;

import RateControl.Models.ApiKey.ApiKey;
import io.lettuce.core.api.StatefulRedisConnection;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class ApiKeyRepository {
    private final StatefulRedisConnection<String, String> redisConnection;

    @Autowired
    public ApiKeyRepository(StatefulRedisConnection<String, String> redisConnection) {
        this.redisConnection = redisConnection;
    }

    public void save(ApiKey apiKey, UUID serviceId) {
        redisConnection.sync().set(apiKey.getApiKey(), serviceId.toString());
    }

    public void save(String apiKey, UUID serviceId) {
        redisConnection.sync().set(apiKey, serviceId.toString());
    }

    public String getByApiKey(String apiKey) {
        return redisConnection.sync().get(apiKey);
    }

    public String getByApiKey(ApiKey apiKey) {
        return redisConnection.sync().get(apiKey.getApiKey());
    }
}
