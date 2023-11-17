package RateControl.Clients;

import RateControl.Exceptions.BadRequestException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

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
