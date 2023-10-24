package RateControl.Controllers;

import RateControl.Exceptions.BadRequestException;
import RateControl.Exceptions.InternalServerException;
import RateControl.Exceptions.UnauthorizedException;
import RateControl.Models.Org.Org;
import RateControl.Security.SecurityService;
import RateControl.Services.APIKeyService;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static RateControl.Security.SecurityService.throwIfNoAuth;

@RestController
@RequestMapping(value = "/auth")
public class APIKeyController {
    private static final Logger log = LogManager.getLogger(APIKeyController.class);

    private final APIKeyService apiKeyService;
    private final SecurityService securityService;

    @Autowired
    public APIKeyController(APIKeyService apiKeyService, SecurityService securityService) {
        this.apiKeyService = apiKeyService;
        this.securityService = securityService;
    }

    @RequestMapping(value = "/{serviceId}", method = POST)
    public ResponseEntity<Optional<String>> createApiKey(@PathVariable UUID serviceId) throws InternalServerException, UnauthorizedException, BadRequestException {
        Optional<Org> org = securityService.getAuthedOrg();
        throwIfNoAuth(org);

        if (serviceId == null) {
            throw new BadRequestException();
        }

        return ResponseEntity.ok(apiKeyService.createApiKey(org.orElseThrow(), serviceId));
    }
}
