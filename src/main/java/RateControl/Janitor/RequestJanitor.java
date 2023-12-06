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

    // Handle api rule stored in cache
    // e.g. if /users has not been hit for 24 hours, the api rule for /users should be removed from cache
    // otherwise should be refreshed etc

    @Scheduled(fixedRate = 3600000) // every minute
    public void removeOldRequests() {
        List<String> requestSets = apiProcessingRepository.getAllApiRequestSets();
        final Double timestampDayAgo = Double.valueOf((Instant.now().minusSeconds(86400)).getEpochSecond());

        for (var i : requestSets) {
            apiProcessingRepository.removeRequests(i, timestampDayAgo);
        }
    }

    @Scheduled(fixedRate = 500) // every half second
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
