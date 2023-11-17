package RateControl.Janitor;

import RateControl.Controllers.ApiProcessingController;
import RateControl.Models.ApiRequest.ApiRequest;
import RateControl.Models.Org.Org;
import RateControl.Repositories.ApiProcessingRepository;
import RateControl.Services.ApiProcessingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

import static RateControl.Config.RedisFormatter.getApiRequestsKey;
import static RateControl.Config.RedisFormatter.getMinuteKeyFromRequestsKey;

@Component
@Configuration
@EnableScheduling
public class RequestJanitor {
    private static final Logger log = LogManager.getLogger(RequestJanitor.class);

    private final ApiProcessingRepository apiProcessingRepository;

    @Autowired
    public RequestJanitor(ApiProcessingRepository apiProcessingRepository) {
        this.apiProcessingRepository = apiProcessingRepository;
    }


    // Remove sorted set entries that are out of date e.g.
    // ZREMRANGEBYSCORE requests_user:1_api:post:/users -inf <TIMESTAMP - 1 Day>

    // Aggregate request values
    // ZCOUNT requests_user:1_api:post:/users 1000 12000
    // ZCOUNT requests_userId:4b7dbb41-4156-4a81-af4d-6052080104c7_api:GET:/users -inf +inf
    // Maybe aggregate once and increment after every request ?

    // Handle api rule stored in cache
    // e.g. if /users has not been hit for 24 hours, the api rule for /users should be removed from cache
    // otherwise should be refreshed etc

    @Scheduled(fixedRate = 1000) // every 1 second
    public void aggregateRequestData() {
        // For every sorted set: requests_user:1_api:post:/users
        // Store aggregated value of total number of requests within the last 60 seconds
        List<String> requestSets = apiProcessingRepository.getAllApiRequestSets();

        for (var i : requestSets) {
            String key = getMinuteKeyFromRequestsKey(i);
            apiProcessingRepository.saveMinuteRequestsValue(key, getNumberOfRequestsLastMinute(i));
        }
    }

    private int getNumberOfRequestsLastMinute(String key) {
        final Double timestampNow = Double.valueOf((Instant.now().getEpochSecond()));
        final Double timestampMinuteAgo = Double.valueOf((Instant.now().minusSeconds(60)).getEpochSecond());

        return apiProcessingRepository.getNumberOfRequests(key, timestampMinuteAgo, timestampNow);
    }
}
