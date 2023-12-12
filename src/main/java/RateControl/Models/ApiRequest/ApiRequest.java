package RateControl.Models.ApiRequest;

import RateControl.Models.ApiKey.ApiKey;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.jsonwebtoken.lang.Strings;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

import static RateControl.Models.ApiRequest.RequestDataType.role;

public class ApiRequest {
    @NotNull
    private String apiKey;

    @NotNull
    private String apiPath;

    @JsonAlias({"userId", "userIp", "data"})
    @NotNull
    @NotBlank
    private String data;

    private String role;

    private UUID orgId;

    private UUID appId;

    private UUID serviceId;

    private UUID apiId;

    @JsonCreator
    public ApiRequest(String apiKey, String apiPath, String data, String role) {
        this.apiKey = apiKey;
        this.apiPath = apiPath;
        this.data = data;
        this.role = role;
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

    public String getRole() {
        return role;
    }

    @JsonIgnore
    public RequestDataType getDataType() {
        if (getRole() != null && Strings.hasText(getRole())) {
            return RequestDataType.role;
        }
        return RequestDataType.from(data);
    }

    @JsonIgnore
    public String getRedisDataString() {
        if (getRole() == null || !Strings.hasText(getRole())) {
            return getData();
        }
        return getData() + "," + getRole();
    }

    public String getApiManagementData() {
        if (getDataType().equals(RequestDataType.role)) {
            return getRole();
        }
        return getData();
    }

    public UUID getOrgId() {
        return orgId;
    }

    public void setOrgId(UUID orgId) {
        this.orgId = orgId;
    }

    public UUID getAppId() {
        return appId;
    }

    public void setAppId(UUID appId) {
        this.appId = appId;
    }

    public UUID getServiceId() {
        return serviceId;
    }

    public void setServiceId(UUID serviceId) {
        this.serviceId = serviceId;
    }

    public UUID getApiId() {
        return apiId;
    }

    public void setApiId(UUID apiId) {
        this.apiId = apiId;
    }

    public boolean isMetaDataPresent() {
        return orgId != null && appId != null && serviceId != null && apiId != null;
    }

    @Override
    public String toString() {
        return "ApiRequest{" +
                "apiKeyLength=" + apiKey.length() +
                ", apiPath=" + apiPath +
                ", dataLength=" + data.length() +
                ", role=" + role +
                '}';
    }
}
