package RateControl.Services;

import RateControl.Clients.RaterManagementClient;
import RateControl.Controllers.OrgController;
import RateControl.Exceptions.InternalServerException;
import RateControl.Exceptions.UnauthorizedException;
import RateControl.Models.ApiKey.ApiKey;
import RateControl.Models.Auth.Auth;
import RateControl.Models.Org.Org;
import RateControl.Repositories.ApiKeyRepository;
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

    private final ApiKeyRepository apiKeyRepository;
    private final OrgService orgService;
    private final RaterManagementClient raterManagementClient;
    private final SecurityService securityService;

    @Autowired
    public APIKeyService(ApiKeyRepository apiKeyRepository, OrgService orgService, RaterManagementClient raterManagementClient, SecurityService securityService) {
        this.apiKeyRepository = apiKeyRepository;
        this.orgService = orgService;
        this.raterManagementClient = raterManagementClient;
        this.securityService = securityService;
    }

    public Optional<String> getServiceIdForApiKey(String apiKey) {
        return Optional.ofNullable(apiKeyRepository.getByApiKey(apiKey));
    }

    public Optional<ApiKey> createApiKey(Org org, UUID serviceId, Auth auth) throws UnauthorizedException {
        // Need to check Org + Service Still exists
        boolean orgServicePairExists = validateServiceId(org, serviceId, auth);

        if (!orgServicePairExists) {
            throw new UnauthorizedException();
        }

        ApiKey apiKey = generateApiKey();

        saveApiKey(apiKey, serviceId);

        return Optional.of(apiKey);
    }


    public void saveApiKey(ApiKey apiKey, UUID serviceId) {
        // if api key already exists for org, serviceId pair then throw bad request

        // save api key
        apiKeyRepository.save(apiKey, serviceId);
    }

    public void saveApiKey(String apiKey, UUID serviceId) {
        // if api key already exists for org, serviceId pair then throw bad request

        // save api key
        apiKeyRepository.save(apiKey, serviceId);
    }

    public String getServiceIdForApiKey(String apiKey) {
        return apiKeyRepository.getByApiKey(apiKey);
    }

    private ApiKey generateApiKey() {
        SecureRandom random = new SecureRandom();
        byte[] keyBytes = new byte[32];
        random.nextBytes(keyBytes);
        return new ApiKey(Base64.getUrlEncoder().encodeToString(keyBytes));
    }

    private boolean validateServiceId(Org org, UUID serviceId, Auth auth) {
        return raterManagementClient.serviceExists(serviceId, org.getId(), auth.getToken());
    }
}
