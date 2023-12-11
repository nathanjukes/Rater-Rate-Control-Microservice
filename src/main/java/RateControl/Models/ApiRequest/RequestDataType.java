package RateControl.Models.ApiRequest;

import java.util.UUID;
import java.util.regex.Pattern;

public enum RequestDataType {
    id, ip, role;

    private static final Pattern IP_ADDRESS_PATTERN = Pattern.compile("^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$");

    public static RequestDataType from(String data) {
        try {
            UUID.fromString(data);
            return id;
        } catch (Exception ex) {

        }

        if (isIpAddress(data)) {
            return ip;
        }

        return role;
    }

    private static boolean isIpAddress(String ip) {
        return IP_ADDRESS_PATTERN.matcher(ip).matches();
    }
}
