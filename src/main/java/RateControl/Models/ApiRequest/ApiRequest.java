package RateControl.Models.ApiRequest;

import RateControl.Models.ApiKey.ApiKey;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class ApiRequest {
    @NotNull
    private String apiKey;

    @NotNull
    private String apiPath;

    @NotNull
    private UUID userId;

    @JsonCreator
    public ApiRequest(String apiKey, String apiPath, UUID userId) {
        this.apiKey = apiKey;
        this.apiPath = apiPath;
        this.userId = userId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getApiPath() {
        return apiPath;
    }

    public UUID getUserId() {
        return userId;
    }

    @JsonIgnore
    public boolean isTypeId() {
        return true;
    }

    @Override
    public String toString() {
        return "ApiRequest{" +
                "apiKeyLength=" + apiKey.length() +
                ", apiPath=" + apiPath +
                ", userIdLength=" + userId.toString().length() +
                '}';
    }
}
