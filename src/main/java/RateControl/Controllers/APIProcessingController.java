package RateControl.Controllers;

import RateControl.Exceptions.InternalServerException;
import RateControl.Exceptions.UnauthorizedException;
import RateControl.Models.ApiKey.ApiKey;
import RateControl.Models.ApiRequest.ApiRequest;
import RateControl.Models.Auth.Auth;
import RateControl.Models.Org.Org;
import RateControl.Security.SecurityService;
import RateControl.Services.APIKeyService;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

import static RateControl.Security.SecurityService.throwIfNoAuth;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/process")
public class APIProcessingController {
    private static final Logger log = LogManager.getLogger(APIProcessingController.class);

    private final SecurityService securityService;

    @Autowired
    public APIProcessingController(SecurityService securityService) {
        this.securityService = securityService;
    }

    @RequestMapping(value = "", method = POST)
    public ResponseEntity<String> processRequest(@RequestBody @Valid ApiRequest apiRequest) {
        // Take in API Request and process it - should respond with valid/invalid
        log.info("Processing API Request: " + apiRequest);

        return ResponseEntity.ok("Processed");
    }

    // Query API for it's current status for a given user
    @RequestMapping(value = "/{apiId}", method = GET)
    public ResponseEntity<String> getApiStatus(@PathVariable UUID apiId, @RequestBody @Valid ApiKey apiKey) throws InternalServerException, UnauthorizedException {
        Optional<Org> org = securityService.getAuthedOrg();
        throwIfNoAuth(org);

        log.info("API Status Requested for: " + apiId);

        // Response
        //

        return ResponseEntity.ok("Status");
    }
}
