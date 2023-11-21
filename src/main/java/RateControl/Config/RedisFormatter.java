package RateControl.Config;

import java.time.Instant;

public class RedisFormatter {
    public final static String REQUESTS_KEYS_QUERY = "requests_userId:*:*:*";

    public final static String getApiRequestsKey(String userId, String apiPath, String apiKey) {
        return String.format("requests_userId:%s_api:%s_apiKey:%s", userId, apiPath, apiKey); // Const this ?
    }

    public final static String getMinuteRequestsKey(String userId, String apiPath, String apiKey) {
        return String.format("minute_requests_userId:%s_api:%s_apiKey:%s", userId, apiPath, apiKey);
    }

    public final static String getMinuteKeyFromRequestsKey(String key) {
        return String.format("minute_" + key);
    }

    public final static String getLimitKey(String key) {
        return String.format("limit_" + key);
    }

    public final static String getCustomLimitKey(String userId, String apiPath, String apiKey) {
        return String.format("limit_requests_userId:%s_api:%s_apiKey:%s", userId, apiPath, apiKey);
    }

    public final static String getBaseLimitKey(String apiPath, String apiKey) {
        return String.format("limit_requests_api:%s_apiKey:%s", apiPath, apiKey);
    }
}
