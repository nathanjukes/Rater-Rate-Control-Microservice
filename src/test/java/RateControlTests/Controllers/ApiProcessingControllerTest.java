package RateControlTests.Controllers;

import RateControl.Controllers.ApiProcessingController;
import RateControl.Exceptions.InternalServerException;
import RateControl.Exceptions.UnauthorizedException;
import RateControl.Models.ApiRequest.ApiRequest;
import RateControl.Models.Auth.Auth;
import RateControl.Models.Org.Org;
import RateControl.Security.SecurityService;
import RateControl.Services.ApiKeyService;
import RateControl.Services.ApiProcessingService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ApiProcessingControllerTest {
    @InjectMocks
    private ApiProcessingController apiProcessingController;
    @Mock
    private ApiProcessingService apiProcessingService;
    @Mock
    private SecurityService securityService;

    private Org testOrg;
    private Auth auth;

    @Before
    public void setup() throws InternalServerException, UnauthorizedException {
        testOrg = new Org("TestOrg");
        testOrg.setId(UUID.randomUUID());
        auth = new Auth("test");

        when(securityService.getAuthedOrg()).thenReturn(Optional.of(testOrg));
        when(securityService.getAuthToken(any())).thenReturn(Optional.of(auth));
    }

    @Test
    public void testProcessRequest() throws InternalServerException, UnauthorizedException, ExecutionException, InterruptedException {
        String apiKey = "apikeytest";
        String apiPath = "/api/test";
        UUID userId = UUID.randomUUID();
        ApiRequest apiRequest = new ApiRequest(apiKey, apiPath, userId);

        apiProcessingController.processRequest(apiRequest, null);

        verify(apiProcessingService, times(1)).processRequest(eq(apiRequest));
        verify(apiProcessingService, times(1)).getApiStatus(eq(apiRequest), eq(true), eq(auth));
    }

    @Test
    public void testGetApiStatus() throws InternalServerException, UnauthorizedException, ExecutionException, InterruptedException {
        String apiKey = "apikeytest";
        String apiPath = "/api/test";
        UUID userId = UUID.randomUUID();
        ApiRequest apiRequest = new ApiRequest(apiKey, apiPath, userId);

        apiProcessingController.getApiStatus(apiRequest, null);

        verify(apiProcessingService, times(1)).getApiStatus(eq(apiRequest), eq(false), eq(auth));
    }
}