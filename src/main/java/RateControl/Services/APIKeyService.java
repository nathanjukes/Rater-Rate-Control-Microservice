package RateControl.Services;

import RateControl.Clients.RaterManagementClient;
import RateControl.Controllers.OrgController;
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

    public Optional<String> createApiKey(Org org, UUID serviceId) {
        // 2 ways of doing this
        // 1 - Access db from here
        // 2 - Call management API

        // 2:
        // Need to check Org + Service Still exists
        boolean orgServicePairExists = validateServiceId(org, serviceId);

        return Optional.of("r");
    }

    private String generateApiKey() {
        SecureRandom random = new SecureRandom();
        byte[] keyBytes = new byte[32];
        random.nextBytes(keyBytes);
        return Base64.getUrlEncoder().encodeToString(keyBytes);
    }

    private boolean validateServiceId(Org org, UUID serviceId) {
        return raterManagementClient.serviceExists(serviceId, securityService.getToken().orElse(""));
    }
}
