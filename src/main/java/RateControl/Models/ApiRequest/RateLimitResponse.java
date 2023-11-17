package RateControl.Models.ApiRequest;

import java.time.LocalDateTime;

public class RateLimitResponse {
    private String apiName;
    private boolean rateExceeded;
    private int apiLimit;
    private int currentLoad;

    public RateLimitResponse(String apiName, boolean rateExceeded, int currentLoad, int apiLimit) {
        this.apiName = apiName;
        this.rateExceeded = rateExceeded;
        this.apiLimit = apiLimit;
        this.currentLoad = currentLoad;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public boolean isRateExceeded() {
        return rateExceeded;
    }

    public void setRateExceeded(boolean rateExceeded) {
        this.rateExceeded = rateExceeded;
    }

    public int getLimit() {
        return apiLimit;
    }

    public void setApiLimit(int apiLimit) {
        this.apiLimit = apiLimit;
    }

    public int getCurrentLoad() {
        return currentLoad;
    }

    public void setCurrentLoad(int currentLoad) {
        this.currentLoad = currentLoad;
    }

    public LocalDateTime getTimeStamp() {
        return LocalDateTime.now();
    }
}
