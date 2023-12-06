package RateControl.Repositories;

import RateControl.Models.ApiKey.ApiKey;
import io.lettuce.core.api.StatefulRedisConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class RedisApiKeyRepository {
    private final StatefulRedisConnection<String, String> redisConnection;
    private final String APIKEY_TO_SERVICEID_KEY = "apikey_to_serviceid";
    @Autowired
    public RedisApiKeyRepository(StatefulRedisConnection<String, String> redisConnection) {
        this.redisConnection = redisConnection;
    }

    public void save(ApiKey apiKey, UUID serviceId) {
        save(apiKey.getApiKey(), serviceId.toString());
    }

    public void save(String apiKey, String serviceId) {
        redisConnection.sync().hset(APIKEY_TO_SERVICEID_KEY, apiKey, serviceId);
    }

    public String getByApiKey(String apiKey) {
        return redisConnection.sync().hget(APIKEY_TO_SERVICEID_KEY, apiKey);
    }
}
