package RateControl.Models.Metrics;

import RateControl.Models.ApiRequest.ApiRequest;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "metrics", indexes = @Index(name = "idx_timestamp", columnList = "timestamp"))
public class RequestMetric {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID apiId;

    private String userData;

    private boolean requestAccepted;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    private UUID orgId;

    private UUID appId;

    private UUID serviceId;

    private int sum;

    public RequestMetric(UUID apiId, UUID orgId, UUID appId, UUID serviceId, String userData, boolean requestAccepted, int sum) {
        this.apiId = apiId;
        this.orgId = orgId;
        this.appId = appId;
        this.serviceId = serviceId;
        this.userData = userData;
        this.requestAccepted = requestAccepted;
        this.timestamp = Date.from(Instant.now());
        this.sum = sum;
    }

    public RequestMetric() {

    }

    public UUID getId() {
        return id;
    }

    public UUID getApiId() {
        return apiId;
    }

    public UUID getOrgId() {
        return orgId;
    }

    public UUID getAppId() {
        return appId;
    }

    public UUID getServiceId() {
        return serviceId;
    }

    public String getUserData() {
        return userData;
    }

    public boolean isRequestAccepted() {
        return requestAccepted;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public int getSum() {
        return sum;
    }

    public static RequestMetric from(ApiRequest apiRequest, boolean requestAccepted) {
        return new RequestMetric(apiRequest.getApiId(), apiRequest.getOrgId(), apiRequest.getAppId(), apiRequest.getServiceId(), apiRequest.getData(), requestAccepted, 1);
    }
}
