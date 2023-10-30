package RateControl.Models.ApiKey;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class CreateApiKeyRequest {
    @NotNull
    private UUID serviceId;

    @JsonCreator
    public CreateApiKeyRequest(UUID serviceId) {
        this.serviceId = serviceId;
    }

    public UUID getServiceId() {
        return serviceId;
    }

    public void setServiceId(UUID serviceId) {
        this.serviceId = serviceId;
    }
}
