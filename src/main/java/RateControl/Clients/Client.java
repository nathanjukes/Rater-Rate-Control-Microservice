package RateControl.Clients;

import RateControl.Exceptions.BadRequestException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class Client {
    protected JSONObject getResource(String url, String auth) throws BadRequestException {
        JSONObject jsonObject = null;
        try {
            HttpResponse response;
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet getConnection = new HttpGet(url);
            getConnection.setHeader("Authorization", "Bearer " + auth);
            try {
                response = httpClient.execute(getConnection);
                String JSONString = EntityUtils.toString(response.getEntity(), "UTF-8");
                jsonObject = new JSONObject(JSONString);
            } catch (Exception e) {
                throw new BadRequestException();
            }
        } catch (Exception e) {
            throw new BadRequestException();
        }
        return jsonObject;
    }

    protected JSONObject getPostResource(String url, String auth, JSONObject requestBody) throws BadRequestException {
        JSONObject jsonObject = null;
        try {
            HttpResponse response;
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost postConnection = new HttpPost(url);
            StringEntity entity = new StringEntity(requestBody.toString());
            entity.setContentType("application/json");
            postConnection.setEntity(entity);

            postConnection.setHeader("Authorization", "Bearer " + auth);
            try {
                response = httpClient.execute(postConnection);
                String JSONString = EntityUtils.toString(response.getEntity(), "UTF-8");
                jsonObject = new JSONObject(JSONString);
            } catch (Exception e) {
                throw new BadRequestException();
            }
        } catch (Exception e) {
            throw new BadRequestException();
        }
        return jsonObject;
    }
}
