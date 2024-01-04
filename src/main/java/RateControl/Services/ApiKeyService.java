package RateControl.Services;

import RateControl.Clients.RaterManagementClient;
import RateControl.Exceptions.BadRequestException;
import RateControl.Exceptions.UnauthorizedException;
import RateControl.Models.ApiKey.ApiKey;
import RateControl.Models.ApiKey.ApiKeyEntity;
import RateControl.Models.Auth.Auth;
import RateControl.Models.Org.Org;
import RateControl.Repositories.RedisApiKeyRepository;
import RateControl.Repositories.PostgresApiKeyRepository;
import RateControl.Security.SecurityService;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ApiKeyService {
    private static final Logger log = LogManager.getLogger(ApiKeyService.class);

    private final RedisApiKeyRepository redisApiKeyRepository;
    private final PostgresApiKeyRepository postgresApiKeyRepository;
    private final RaterManagementClient raterManagementClient;
    private final SecurityService securityService;

    @Autowired
    public ApiKeyService(RedisApiKeyRepository redisApiKeyRepository, PostgresApiKeyRepository postgresApiKeyRepository, RaterManagementClient raterManagementClient, SecurityService securityService) {
        this.redisApiKeyRepository = redisApiKeyRepository;
        this.postgresApiKeyRepository = postgresApiKeyRepository;
        this.raterManagementClient = raterManagementClient;
        this.securityService = securityService;
    }

    public Optional<ApiKey> createApiKey(Org org, UUID serviceId, Auth auth) throws UnauthorizedException, BadRequestException, NoSuchAlgorithmException {
        // Need to check Org + Service Still exists
        boolean orgServicePairExists = validateServiceId(org, serviceId, auth);

        if (!orgServicePairExists) {
            throw new UnauthorizedException();
        }

        ApiKey apiKey = generateApiKey();

        log.info("Saving API Key for orgId: {} serviceId: {}", org.getId(), serviceId);
        saveApiKey(apiKey, serviceId);

        return Optional.of(apiKey);
    }


    private void saveApiKey(ApiKey apiKey, UUID serviceId) throws BadRequestException {
        // Validate that api key does not already exist for serviceId
        if (postgresApiKeyRepository.getByServiceId(serviceId).isPresent()) {
            log.info("API Key already exists for serviceId: {}", serviceId);
            throw new BadRequestException();
        }

        // save APIKey/ServiceId pair
        postgresApiKeyRepository.save(new ApiKeyEntity(apiKey, serviceId));
        redisApiKeyRepository.save(apiKey, serviceId);
    }

    public String getServiceIdForApiKey(String apiKey) throws BadRequestException {
        String serviceId = redisApiKeyRepository.getByApiKey(apiKey);
        if (serviceId != null) {
            return serviceId;
        }

        serviceId = postgresApiKeyRepository.getByApiKey(apiKey)
                .map(ApiKeyEntity::getServiceId)
                .map(s -> s.toString())
                .orElseThrow(BadRequestException::new);
        redisApiKeyRepository.save(apiKey, serviceId);

        return serviceId;
    }

    public Optional<ApiKeyEntity> getApiKeyForServiceId(String serviceId) {
        return postgresApiKeyRepository.getByServiceId(UUID.fromString(serviceId));
    }

    private ApiKey generateApiKey() throws NoSuchAlgorithmException {
        byte[] bytes = new byte[32];
        SecureRandom.getInstanceStrong().nextBytes(bytes);
        return new ApiKey(Base64.getUrlEncoder().encodeToString(bytes));
    }

    public boolean validateServiceId(Org org, UUID serviceId, Auth auth) throws BadRequestException {
        log.info("Validating service id exists for orgId: {} serviceId: {}", org.getId(), serviceId);
        return raterManagementClient.serviceExists(serviceId, org.getId(), auth.getToken());
    }

    public boolean validateApiKey(Org org, String apiKey, Auth auth) throws BadRequestException {
        log.info("Validating api key exists for orgId: {}", org.getId());
        UUID serviceId = UUID.fromString(getServiceIdForApiKey(apiKey));
        return raterManagementClient.serviceExists(serviceId, org.getId(), auth.getToken());
    }

    public void deleteApiKey(String apiKey) {
        redisApiKeyRepository.delete(apiKey);
        postgresApiKeyRepository.deleteByApiKey(apiKey);
    }
}
