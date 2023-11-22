package RateControl.Models.Health;

import java.time.Instant;
import java.util.Date;

public class HealthObject {
    private final String healthStatus;

    public HealthObject(String status) {
        this.healthStatus = status;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public Date getTimestamp() {
        return Date.from(Instant.now());
    }
}