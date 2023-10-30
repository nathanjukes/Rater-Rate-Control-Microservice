package RateControl.Models.ApiKey;

import jakarta.validation.constraints.NotBlank;

public class ApiKey {
    @NotBlank
    private String apiKey;

    public ApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiKey() {
        return apiKey;
    }
}
