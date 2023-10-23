package RateControl.Models;

import java.time.LocalDateTime;

public class RateLimitResponse {
    private String apiName;
    private String flatStructure;
    private boolean rateExceeded;
    private int apiLimit;
    private int currentLoad;

    public RateLimitResponse(String apiName, String flatStructure, boolean rateExceeded, int apiLimit, int currentLoad) {
        this.apiName = apiName;
        this.flatStructure = flatStructure;
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

    public String getFlatStructure() {
        return flatStructure;
    }

    public void setFlatStructure(String flatStructure) {
        this.flatStructure = flatStructure;
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
