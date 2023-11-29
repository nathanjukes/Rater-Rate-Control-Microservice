package RateControlTests.Controllers;

import RateControl.Controllers.ApiProcessingController;
import RateControl.Exceptions.InternalServerException;
import RateControl.Exceptions.UnauthorizedException;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApiProcessingControllerTest {
    @InjectMocks
    private ApiProcessingController apiProcessingController;
    @Mock
    private ApiKeyService apiKeyService;
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

    }

    @Test
    public void testProcessRequest() {

    }
}
