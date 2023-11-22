package RateControl.Controllers;

import RateControl.Models.Health.HealthObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/health")
public class HealthController {
    @RequestMapping(method = GET)
    public ResponseEntity<HealthObject> getHealth() {
        return ResponseEntity.ok(new HealthObject("healthy"));
    }
}
