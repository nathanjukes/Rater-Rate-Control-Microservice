package RateControl.Clients;

import RateControl.Exceptions.BadRequestException;
import RateControl.Models.ApiLimit.ApiLimitResponse;
import RateControl.Models.ApiLimit.CustomRuleType;
import RateControl.Models.ApiRequest.ApiRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static RateControl.Models.ApiRequest.RequestDataType.role;

@Component
public class RaterManagementClient extends Client {
    @Value("${rater.management.host}")
    private String RATER_MANAGEMENT_HOST;

    @Value("${rater.management.port}")
    private String RATER_MANAGEMENT_PORT;

    private static final Logger log = LogManager.getLogger(RaterManagementClient.class);

    private final String RATER_MANAGEMENT_API = "rater/api";
    private final String API_Version = "v1";
    private final String SERVICES_URI = "services";
    private final String APIS_URI = "apis";
    private final String RULES_URI = "rules";
    private final String RULES_SEARCH_URI = "rules/search";
    private final String BAD_REQUEST_RESPONSE = "Bad Request";

    public boolean serviceExists(UUID serviceId, UUID orgId, String auth) throws BadRequestException {
        final String url = getBaseUrl() + "/" + SERVICES_URI + "/" + serviceId;
        JSONObject jsonObject;

        try {
            jsonObject = getResource(url, auth);
            if (jsonObject == null || jsonObject.isEmpty()) {
                return false;
            }
        } catch (BadRequestException ex) {
            log.info("Bad request: Service id does not exist for orgId: {} serviceId: {}", orgId, serviceId);
            throw ex;
        }

        // Try to get orgId from service
        // Should not really be able to ever fail here - with the user token they should only see orgId that they are valid for
        UUID orgIdReturned = UUID.fromString((String) jsonObject.get("orgId"));

        return orgId.equals(orgIdReturned);
    }

    public Optional<ApiLimitResponse> getApiSearchRule(ApiRequest apiRequest, String serviceId, String auth) throws BadRequestException {
        JSONObject requestBody = new JSONObject();
        requestBody.put("data", apiRequest.getApiManagementData());
        requestBody.put("type", apiRequest.getDataType().toString());
        requestBody.put("apiPath", apiRequest.getApiPath());
        requestBody.put("serviceId", serviceId);

        try {
            return getApiSearchRule(requestBody, auth);
        } catch (BadRequestException ex) {
            log.info("Bad request: Cannot get api rule for apiId: {} serviceId: {]", apiRequest.getApiPath(), serviceId);
            throw ex;
        }
    }

    @Deprecated
    private int getApiRule(JSONObject requestBody, String auth) throws BadRequestException {
        final String url = getBaseUrl() + "/" + APIS_URI + "/" + RULES_URI;
        JSONObject jsonObject;

        jsonObject = getPostResource(url, auth, requestBody);
        if (jsonObject == null || jsonObject.isEmpty()) {
            return 0;
        }

        return (int) jsonObject.get("useLimit");
    }

    private Optional<ApiLimitResponse> getApiSearchRule(JSONObject requestBody, String auth) throws BadRequestException {
        final String url = getBaseUrl() + "/" + APIS_URI + "/" + RULES_SEARCH_URI;
        JSONObject jsonObject;

        jsonObject = getPostResource(url, auth, requestBody);
        if (jsonObject == null || jsonObject.isEmpty() || BAD_REQUEST_RESPONSE.equals(jsonObject.optString("error"))) {
            throw new BadRequestException("Rule not found");
        }

        int useLimit = (int) jsonObject.getJSONObject("rule").get("useLimit");
        CustomRuleType customRuleType = CustomRuleType.valueOf((String) jsonObject.getJSONObject("rule").get("customRuleType"));
        UUID orgId = UUID.fromString((String) jsonObject.get("orgId"));
        UUID appId = UUID.fromString((String) jsonObject.get("appId"));
        UUID serviceId = UUID.fromString((String) jsonObject.get("serviceId"));
        UUID apiId = UUID.fromString((String) jsonObject.get("apiId"));

        return Optional.of(new ApiLimitResponse(useLimit, customRuleType, orgId, appId, serviceId, apiId));
    }

    private String getBaseUrl() {
        return getRaterManagementUrl() + "/" + API_Version;
    }

    private String getRaterManagementUrl() {
        String url = RATER_MANAGEMENT_HOST + ":" + RATER_MANAGEMENT_PORT + "/" + RATER_MANAGEMENT_API;
        if (RATER_MANAGEMENT_HOST.equals("localhost")) {
            return "http://" + url;
        }
        // Should be https when the service has a certificate
        return "http://" + url;
    }
}
