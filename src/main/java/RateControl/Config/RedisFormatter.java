package RateControl.Config;

import java.time.Instant;

public class RedisFormatter {
    public final static String getApiRequestsKey(String userId, String apiPath) {
        return String.format("requests_userId:%s_api:%s", userId, apiPath); // Const this ?
    }

    public final static String getMinuteRequestsKey(String userId, String apiPath) {
        return String.format("minute_requests_userId:%s_api:%s", userId, apiPath);
    }

    public final static String getMinuteKeyFromRequestsKey(String key) {
        return String.format("minute_" + key);
    }
}
