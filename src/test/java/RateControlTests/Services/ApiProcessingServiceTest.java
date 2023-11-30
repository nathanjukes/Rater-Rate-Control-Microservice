package RateControlTests.Services;

import RateControl.Clients.RaterManagementClient;
import RateControl.Models.ApiRequest.ApiRequest;
import RateControl.Models.Auth.Auth;
import RateControl.Models.Org.Org;
import RateControl.Repositories.ApiProcessingRepository;
import RateControl.Services.ApiKeyService;
import RateControl.Services.ApiProcessingService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ApiProcessingServiceTest {
    @InjectMocks
    private ApiProcessingService apiProcessingService;
    @Mock
    private ApiProcessingRepository apiProcessingRepository;
    @Mock
    private ApiKeyService apiKeyService;

    private Org testOrg;
    private Auth auth;

    @Before
    public void setup() {
        testOrg = new Org("TestOrg");
        testOrg.setId(UUID.randomUUID());
        auth = new Auth("test");
    }

    @Test
    public void testProcessRequest() {
        UUID userId = UUID.randomUUID();
        String apiKey = "apiKeyTest";
        String apiPath = "apiPathTest";
        String redisKey = String.format("requests_userId:%s_api:%s_apiKey:%s", userId, apiPath, apiKey);
        ApiRequest apiRequest = new ApiRequest(apiKey, apiPath, userId);

        apiProcessingService.processRequest(apiRequest);

        verify(apiProcessingRepository, times(1)).saveRequest(eq(redisKey));
    }

    @Test
    public void testGetApiStatus() throws ExecutionException, InterruptedException {
        UUID userId = UUID.randomUUID();
        String apiKey = "apiKeyTest";
        String apiPath = "apiPathTest";
        String redisKey = String.format("minute_requests_userId:%s_api:%s_apiKey:%s", userId, apiPath, apiKey);
        ApiRequest apiRequest = new ApiRequest(apiKey, apiPath, userId);

        apiProcessingService.getApiStatus(apiRequest, false, auth);

        verify(apiProcessingRepository, times(1)).getMinuteRequests(eq(redisKey));
        verify(apiKeyService, times(1)).getServiceIdForApiKey(apiRequest.getApiKey());
    }
}
