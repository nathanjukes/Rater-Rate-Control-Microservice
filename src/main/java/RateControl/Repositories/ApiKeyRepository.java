package RateControl.Repositories;

import RateControl.Models.ApiKey.ApiKey;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class ApiKeyRepository {
    @Resource(name = "redisTemplate")
    private RedisTemplate<String, String> apiKeyRedisTemplate;

    public void save(ApiKey apiKey, UUID serviceId) {
        apiKeyRedisTemplate.opsForValue().set(apiKey.getApiKey(), serviceId.toString());
    }

    public String getByApiKey(String apiKey) {
        return apiKeyRedisTemplate.opsForValue().get(apiKey);
    }

    public String getByApiKey(ApiKey apiKey) {
        return apiKeyRedisTemplate.opsForValue().get(apiKey.getApiKey());
    }
}
