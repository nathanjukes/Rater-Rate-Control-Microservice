package RateControl.Controllers;

import RateControl.Exceptions.BadRequestException;
import RateControl.Exceptions.DataConflictException;
import RateControl.Exceptions.InternalServerException;
import RateControl.Exceptions.UnauthorizedException;
import RateControl.Models.Org.Org;
import RateControl.Models.Org.OrgCreateRequest;
import RateControl.Models.User.User;
import RateControl.Security.SecurityService;
import RateControl.Services.OrgService;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping("/orgs")
public class OrgController {
    private static final Logger log = LogManager.getLogger(OrgController.class);

    private final OrgService orgService;
    private final SecurityService securityService;

    @Autowired
    public OrgController(OrgService orgService, SecurityService securityService) {
        this.orgService = orgService;
        this.securityService = securityService;
    }

    @RequestMapping(value = "", method = POST)
    public ResponseEntity<Optional<Org>> createOrg(@RequestBody @Valid OrgCreateRequest orgCreateRequest) throws DataConflictException, InternalServerException, BadRequestException {
        log.info("Create Org Request: " + orgCreateRequest);

        return ResponseEntity.ok(orgService.createOrg(orgCreateRequest));
    }

    @RequestMapping(value = "/{org}", method = GET)
    public ResponseEntity<Optional<Org>> getOrg(@PathVariable String org) {
        return ResponseEntity.ok(orgService.getOrg(org));
    }

    @CrossOrigin
    @RequestMapping(value = "", method = GET)
    public ResponseEntity<?> getOrgs(@RequestParam(required = false) UUID orgId) throws InternalServerException, UnauthorizedException {
        Optional<User> user = securityService.getAuthedUser();
        if (orgId != null) {
            return ResponseEntity.ok(orgService.getOrg(orgId));
        }
        return ResponseEntity.ok(orgService.getOrgs());
    }
}
