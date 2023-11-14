package RateControl.Models.ApiKey;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotBlank;

public class ApiKey {
    @NotBlank
    private String apiKey;

    @JsonCreator
    public ApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiKey() {
        return apiKey;
    }
}
