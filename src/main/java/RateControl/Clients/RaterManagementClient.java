package RateControl.Clients;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RaterManagementClient extends Client {
    @Value("${rater.management.url}")
    private String RATER_MANAGEMENT_URL;

    private final String API_Version = "v1";
    private final String SERVICES_URI = "services";

    public boolean serviceExists(UUID serviceId, UUID orgId, String auth) {
        final String url = getBaseUrl() + "/" + SERVICES_URI + "/" + serviceId;

        JSONObject jsonObject = getResource(url, auth);
        if (jsonObject == null || jsonObject.isEmpty()) {
            return false;
        }

        // Try to get orgId from service
        // Should not really be able to ever fail here - with the user token they should only see orgId that they are valid for
        UUID orgIdReturned = UUID.fromString((String) jsonObject.get("orgId"));

        return orgId.equals(orgIdReturned);
    }

    private String getBaseUrl() {
        return RATER_MANAGEMENT_URL + "/" + API_Version;
    }
}
