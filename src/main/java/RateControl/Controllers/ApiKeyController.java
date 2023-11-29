package RateControl.Controllers;

import RateControl.Exceptions.BadRequestException;
import RateControl.Exceptions.InternalServerException;
import RateControl.Exceptions.UnauthorizedException;
import RateControl.Models.ApiKey.ApiKey;
import RateControl.Models.ApiKey.CreateApiKeyRequest;
import RateControl.Models.Auth.Auth;
import RateControl.Models.Org.Org;
import RateControl.Security.SecurityService;
import RateControl.Services.ApiKeyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static RateControl.Security.SecurityService.throwIfNoAuth;

@RestController
@RequestMapping(value = "/auth")
public class ApiKeyController {
    private static final Logger log = LogManager.getLogger(ApiKeyController.class);

    private final ApiKeyService apiKeyService;
    private final SecurityService securityService;

    @Autowired
    public ApiKeyController(ApiKeyService apiKeyService, SecurityService securityService) {
        this.apiKeyService = apiKeyService;
        this.securityService = securityService;
    }

    @RequestMapping(value = "/{apiKey}", method = GET)
    public ResponseEntity<String> getApiKeyValue(@PathVariable String apiKey) {
        return ResponseEntity.ok(apiKeyService.getServiceIdForApiKey(apiKey));
    }

    @CrossOrigin
    @RequestMapping(value = "/keys/{serviceId}", method = GET)
    public ResponseEntity<String> getApiKeyForService(@PathVariable String serviceId) {
        return ResponseEntity.ok(apiKeyService.getApiKeyForServiceId(serviceId));
    }

    @CrossOrigin
    @RequestMapping(value = "", method = POST)
    public ResponseEntity<Optional<ApiKey>> createApiKey(@RequestBody @Valid CreateApiKeyRequest apiKeyRequest, HttpServletRequest servletRequest) throws InternalServerException, UnauthorizedException, BadRequestException, NoSuchAlgorithmException {
        Optional<Auth> auth = securityService.getAuthToken(servletRequest);
        Optional<Org> org = securityService.getAuthedOrg();
        throwIfNoAuth(org);

        if (apiKeyRequest.getServiceId() == null) {
            throw new BadRequestException();
        }

        log.info("Generating API Key for orgId: {} serviceId: {}", org.map(Org::getId).orElseThrow(), apiKeyRequest.getServiceId());

        // Generates ApiKey for Org/ServiceId Pair - validates serviceId exists and belongs to Org given
        Optional<ApiKey> apiKey = apiKeyService.createApiKey(
                org.orElseThrow(),
                apiKeyRequest.getServiceId(),
                auth.orElseThrow()
        );

        return ResponseEntity.ok(apiKey);
    }
}
