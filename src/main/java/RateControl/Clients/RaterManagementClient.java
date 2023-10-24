package RateControl.Clients;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RaterManagementClient {
    @Value("${rater.management.url}")
    private String RATER_MANAGEMENT_URL;

    private String API_Version = "v1";

    private String getBaseUrl() {
        return RATER_MANAGEMENT_URL + "/" + API_Version;
    }

    public boolean serviceExists(UUID serviceId, String auth) {
        String url = getBaseUrl() + "/services" + "/" + serviceId;

        JSONObject jsonObject = getResource(url, auth);

        return !jsonObject.isEmpty();
    }

    public boolean orgExists(UUID orgId) {
        return false;
    }

    private JSONObject getResource(String url, String auth) {
        JSONObject jsonObject = null;
        try {
            HttpResponse response;
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet getConnection = new HttpGet(url);
            getConnection.setHeader("Authorization", "Bearer " + "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0RW1haWwyQGdtYWlsLmNvbSIsImlhdCI6MTY5ODE0OTQyMywiZXhwIjoxNjk4MjM1ODIzfQ.UNooP_YLvsI67nWpnHCbs9X-6mItddl_zVA6YyPeHyM85HsLVThdeusWoPsZOmm8urQHw5iiXI2-cEVvhs2qng");
            try {
                response = httpClient.execute(getConnection);
                String JSONString = EntityUtils.toString(response.getEntity(), "UTF-8");
                jsonObject = new JSONObject(JSONString);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
