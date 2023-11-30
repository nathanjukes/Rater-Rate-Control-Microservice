package RateControlTests.Services;

import RateControl.Clients.RaterManagementClient;
import RateControl.Exceptions.BadRequestException;
import RateControl.Exceptions.UnauthorizedException;
import RateControl.Models.ApiKey.ApiKey;
import RateControl.Models.Auth.Auth;
import RateControl.Models.Org.Org;
import RateControl.Repositories.ApiKeyRepository;
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
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class ApiKeyServiceTest {
    @InjectMocks
    private ApiKeyService apiKeyService;
    @Mock
    private ApiKeyRepository apiKeyRepository;
    @Mock
    private RaterManagementClient raterManagementClient;

    private Org testOrg;
    private Auth auth;

    @Before
    public void setup() {
        testOrg = new Org("TestOrg");
        testOrg.setId(UUID.randomUUID());
        auth = new Auth("test");
    }

    @Test
    public void testCreateApiKey() throws UnauthorizedException, BadRequestException, NoSuchAlgorithmException {
        UUID serviceId = UUID.randomUUID();

        when(raterManagementClient.serviceExists(serviceId, testOrg.getId(), auth.getToken())).thenReturn(true);
        when(apiKeyRepository.apiKeyExistsForServiceId(anyString())).thenReturn(false);

        Optional<ApiKey> apiKey = apiKeyService.createApiKey(testOrg, serviceId, auth);

        assertTrue(apiKey.isPresent());
        verify(apiKeyRepository, times(1)).save(any(ApiKey.class), eq(serviceId));
    }

    @Test
    public void testCreateApiKeyAlreadyExists() throws BadRequestException {
        UUID serviceId = UUID.randomUUID();

        when(raterManagementClient.serviceExists(serviceId, testOrg.getId(), auth.getToken())).thenReturn(true);
        when(apiKeyRepository.apiKeyExistsForServiceId(anyString())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> apiKeyService.createApiKey(testOrg, serviceId, auth));
        verify(apiKeyRepository, times(0)).save(any(ApiKey.class), any());
        verify(apiKeyRepository, times(0)).save(any(String.class), any());
    }

    @Test
    public void testCreateApiKeyServiceDoesNotExist() throws BadRequestException {
        UUID serviceId = UUID.randomUUID();

        when(raterManagementClient.serviceExists(serviceId, testOrg.getId(), auth.getToken())).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> apiKeyService.createApiKey(testOrg, serviceId, auth));
        verify(apiKeyRepository, times(0)).save(any(ApiKey.class), any());
        verify(apiKeyRepository, times(0)).save(any(String.class), any());
    }
}