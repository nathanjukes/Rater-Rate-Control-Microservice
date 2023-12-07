package RateControl.Models.ApiRequest;

import RateControl.Models.ApiKey.ApiKey;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class ApiRequest {
    @NotNull
    private String apiKey;

    @NotNull
    private String apiPath;

    @JsonAlias({"userId", "userIp", "data"})
    @NotNull
    @NotBlank
    private String data;

    @JsonCreator
    public ApiRequest(String apiKey, String apiPath, String data) {
        this.apiKey = apiKey;
        this.apiPath = apiPath;
        this.data = data;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getApiPath() {
        return apiPath;
    }

    public String getData() {
        return data;
    }

    @JsonIgnore
    public RequestDataType getDataType() {
        return RequestDataType.from(data);
    }

    @Override
    public String toString() {
        return "ApiRequest{" +
                "apiKeyLength=" + apiKey.length() +
                ", apiPath=" + apiPath +
                ", dataLength=" + data.length() +
                '}';
    }
}
