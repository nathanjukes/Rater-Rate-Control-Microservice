package RateControl.Models.ApiRequest;

import RateControl.Models.ApiKey.ApiKey;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class ApiRequest {
    @NotNull
    private ApiKey apiKey;

    @NotNull
    private String apiPath;

    @NotNull
    private UUID userId;

    @JsonCreator
    public ApiRequest(@Valid ApiKey apiKey, String apiPath, UUID userId) {
        this.apiKey = apiKey;
        this.apiPath = apiPath;
        this.userId = userId;
    }

    public ApiKey getApiKey() {
        return apiKey;
    }

    public String getApiPath() {
        return apiPath;
    }

    public UUID getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "ApiRequest{" +
                "apiKeyLength=" + apiKey.getApiKey().length() +
                ", apiPath=" + apiPath +
                ", userIdLength=" + userId.toString().length() +
                '}';
    }
}
