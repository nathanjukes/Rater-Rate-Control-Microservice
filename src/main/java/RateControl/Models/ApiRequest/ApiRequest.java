package RateControl.Models.ApiRequest;

import RateControl.Models.ApiKey.ApiKey;
import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class ApiRequest {
    private ApiKey apiKey;

    @NotNull
    private UUID apiId;

    @NotNull
    private UUID userId;

    @JsonCreator
    public ApiRequest(@Valid ApiKey apiKey, UUID apiId, UUID userId) {
        this.apiKey = apiKey;
        this.apiId = apiId;
        this.userId = userId;
    }

    public ApiKey getApiKey() {
        return apiKey;
    }

    public UUID getApiId() {
        return apiId;
    }

    public UUID getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "ApiRequest{" +
                "apiKeyLength=" + apiKey.getApiKey().length() +
                ", apiId=" + apiId +
                ", userIdLength=" + userId.toString().length() +
                '}';
    }
}
