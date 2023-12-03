package RateControl.Models.ApiKey;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "apiKeys", uniqueConstraints=@UniqueConstraint(columnNames = {"apiKey"}))
public class ApiKeyEntity {
    @Id
    private String apiKey;

    @Column(unique = true)
    private UUID serviceId;

    public ApiKeyEntity(String apiKey, UUID serviceId) {
        this.apiKey = apiKey;
        this.serviceId = serviceId;
    }

    public ApiKeyEntity(ApiKey apiKey, UUID serviceId) {
        this.apiKey = apiKey.getApiKey();
        this.serviceId = serviceId;
    }

    public ApiKeyEntity() {

    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public UUID getServiceId() {
        return serviceId;
    }

    public void setServiceId(UUID serviceId) {
        this.serviceId = serviceId;
    }
}
