package RateControl.Config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {
    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private String redisPort;

    @Value("${spring.data.redis.password}")
    private String redisPassword;

    @Bean
    public StatefulRedisConnection<String, String> redisConnection() {
        RedisURI redisUri;

        if (redisPassword != null && !redisPassword.isBlank()) {
            redisUri = RedisURI.builder()
                    .withHost(redisHost)
                    .withPort(Integer.parseInt(redisPort))
                    .withPassword(redisPassword)
                    .build();
        } else {
            redisUri = RedisURI.builder()
                    .withHost(redisHost)
                    .withPort(Integer.parseInt(redisPort))
                    .build();
        }

        RedisClient redisClient = RedisClient.create(redisUri);
        return redisClient.connect();
    }
}
