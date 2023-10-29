package RateControl.Services;

import RateControl.Clients.RaterManagementClient;
import RateControl.Controllers.OrgController;
import RateControl.Exceptions.UnauthorizedException;
import RateControl.Models.Auth.Auth;
import RateControl.Models.Org.Org;
import RateControl.Security.SecurityService;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class APIKeyService {
    private static final Logger log = LogManager.getLogger(APIKeyService.class);

    private final OrgService orgService;
    private final RaterManagementClient raterManagementClient;
    private final SecurityService securityService;

    @Autowired
    public APIKeyService(OrgService orgService, RaterManagementClient raterManagementClient, SecurityService securityService) {
        this.orgService = orgService;
        this.raterManagementClient = raterManagementClient;
        this.securityService = securityService;
    }

    public Optional<String> createApiKey(Org org, UUID serviceId, Auth auth) throws UnauthorizedException {
        // Need to check Org + Service Still exists
        boolean orgServicePairExists = validateServiceId(org, serviceId, auth);

        if (!orgServicePairExists) {
            throw new UnauthorizedException();
        }

        return Optional.of("valid");
    }

    private String generateApiKey() {
        SecureRandom random = new SecureRandom();
        byte[] keyBytes = new byte[32];
        random.nextBytes(keyBytes);
        return Base64.getUrlEncoder().encodeToString(keyBytes);
    }

    private boolean validateServiceId(Org org, UUID serviceId, Auth auth) {
        return raterManagementClient.serviceExists(serviceId, org.getId(), auth.getToken());
    }
}
