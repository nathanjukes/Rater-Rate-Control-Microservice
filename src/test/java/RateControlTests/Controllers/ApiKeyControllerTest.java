package RateControlTests.Controllers;

import RateControl.Controllers.ApiKeyController;
import RateControl.Exceptions.BadRequestException;
import RateControl.Exceptions.InternalServerException;
import RateControl.Exceptions.UnauthorizedException;
import RateControl.Models.ApiKey.CreateApiKeyRequest;
import RateControl.Models.Auth.Auth;
import RateControl.Models.Org.Org;
import RateControl.Security.SecurityService;
import RateControl.Services.ApiKeyService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ApiKeyControllerTest {
    @InjectMocks
    private ApiKeyController apiKeyController;
    @Mock
    private ApiKeyService apiKeyService;
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
    public void testCreateApiKey() throws InternalServerException, UnauthorizedException, BadRequestException, NoSuchAlgorithmException {
        UUID serviceId = UUID.randomUUID();
        CreateApiKeyRequest apiKeyRequest = new CreateApiKeyRequest(serviceId);
        apiKeyController.createApiKey(apiKeyRequest, null);

        verify(apiKeyService, times(1)).createApiKey(eq(testOrg), eq(serviceId), eq(auth));
    }

    @Test
    public void testCreateApiKeyNullServiceId() throws UnauthorizedException, BadRequestException, NoSuchAlgorithmException {
        UUID serviceId = UUID.randomUUID();
        CreateApiKeyRequest apiKeyRequest = new CreateApiKeyRequest(null);

        assertThrows(
                BadRequestException.class,
                () -> apiKeyController.createApiKey(apiKeyRequest, null)
        );

        verify(apiKeyService, times(0)).createApiKey(eq(testOrg), eq(serviceId), eq(auth));
    }

    @Test
    public void testGetApiKey() {
        String apiKey = "testApiKey";
        apiKeyController.getApiKeyValue(apiKey);
        verify(apiKeyService, times(1)).getServiceIdForApiKey(eq(apiKey));
    }
}
