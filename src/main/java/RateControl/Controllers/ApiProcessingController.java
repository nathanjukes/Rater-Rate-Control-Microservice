package RateControl.Controllers;

import RateControl.Exceptions.InternalServerException;
import RateControl.Exceptions.UnauthorizedException;
import RateControl.Models.ApiKey.ApiKey;
import RateControl.Models.ApiRequest.ApiRequest;
import RateControl.Models.ApiRequest.RateLimitResponse;
import RateControl.Models.Auth.Auth;
import RateControl.Models.Org.Org;
import RateControl.Security.SecurityService;
import RateControl.Services.ApiProcessingService;
import jakarta.servlet.http.HttpServletRequest;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static RateControl.Security.SecurityService.throwIfNoAuth;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/process")
public class ApiProcessingController {
    private static final Logger log = LogManager.getLogger(ApiProcessingController.class);

    private final ApiProcessingService apiProcessingService;
    private final SecurityService securityService;

    @Autowired
    public ApiProcessingController(ApiProcessingService apiProcessingService, SecurityService securityService) {
        this.apiProcessingService = apiProcessingService;
        this.securityService = securityService;
    }

    @RequestMapping(value = "", method = POST)
    public ResponseEntity<RateLimitResponse> processRequest(@RequestBody @Valid ApiRequest apiRequest, HttpServletRequest servletRequest) throws InternalServerException, UnauthorizedException, ExecutionException, InterruptedException {
        Optional<Org> org = securityService.getAuthedOrg();
        Optional<Auth> auth = securityService.getAuthToken(servletRequest);
        throwIfNoAuth(org);

        log.info("Processing API Request: " + apiRequest);

        CompletableFuture.runAsync(() -> apiProcessingService.processRequest(apiRequest));
        CompletableFuture<RateLimitResponse> rateLimitResponse = CompletableFuture.supplyAsync(() -> {
            try {
                return apiProcessingService.getApiStatus(apiRequest, true, auth.orElseThrow());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        return ResponseEntity.ok(rateLimitResponse.get());
    }

    @RequestMapping(value = "", method = GET)
    public ResponseEntity<RateLimitResponse> getApiStatus(@RequestBody @Valid ApiRequest apiRequest, HttpServletRequest servletRequest) throws InternalServerException, UnauthorizedException, ExecutionException, InterruptedException {
        Optional<Org> org = securityService.getAuthedOrg();
        Optional<Auth> auth = securityService.getAuthToken(servletRequest);
        throwIfNoAuth(org);

        log.info("API Status Requested for: " + apiRequest.getApiPath());

        return ResponseEntity.ok(apiProcessingService.getApiStatus(apiRequest, false, auth.orElseThrow()));
    }
}
