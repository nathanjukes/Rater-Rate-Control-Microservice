package RateControlTests.Config;

import RateControl.Config.RedisFormatter;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;
import java.util.stream.Stream;

import static org.springframework.test.util.AssertionErrors.assertEquals;

@RunWith(MockitoJUnitRunner.Silent.class)

public class RedisFormatterTest {

    @Test
    public void testGetApiRequestsKey() {
        String key = "requests_userId:test_api:/api_apiKey:testApi";
        String out = RedisFormatter.getApiRequestsKey("test", "/api", "testApi");
        assertEquals("", key, out);
    }

    @Test
    public void testGetMinuteRequestsKey() {
        String key = "minute_requests_userId:test_api:/api_apiKey:testApi";
        String out = RedisFormatter.getMinuteRequestsKey("test", "/api", "testApi");
        assertEquals("", key, out);
    }

    @Test
    public void testGetMinuteKeyFromRequestsKey() {
        String key = "minute_requests_userId:test_api:/api_apiKey:testApi";
        String out = RedisFormatter.getMinuteRequestsKey("test", "/api", "testApi");
        assertEquals("", key, out);
    }

    @Test
    public void testGetLimitKey() {
        String key = "limit_testApi";
        String out = RedisFormatter.getLimitKey("testApi");
        assertEquals("", key, out);
    }

    @Test
    public void testGetCustomLimitKey() {
        String key = "limit_requests_userId:test_api:/api_apiKey:testApi";
        String out = RedisFormatter.getCustomLimitKey("test", "/api", "testApi");
        assertEquals("", key, out);
    }

    @Test
    public void testGtBaseLimitKey() {
        String key = "limit_requests_api:apiPath_apiKey:testApi";
        String out = RedisFormatter.getBaseLimitKey("apiPath", "testApi");
        assertEquals("", key, out);
    }
}
