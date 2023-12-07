package RateControl.Models.ApiRequest;

import java.util.UUID;

public enum RequestDataType {
    id, ip;

    public static RequestDataType from(String data) {
        try {
            UUID.fromString(data);
            return id;
        } catch (Exception ex) {

        }

        return ip;
    }
}
