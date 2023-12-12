package RateControl.Models.ApiLimit;

import java.util.Optional;
import java.util.UUID;

public class ApiLimitResponse {
    private int useLimit;

    private CustomRuleType customRuleType;

    private UUID orgId;

    private UUID appId;

    private UUID serviceId;

    private UUID apiId;

    public ApiLimitResponse(int useLimit, CustomRuleType customRuleType, UUID orgId, UUID appId, UUID serviceId, UUID apiId) {
        this.useLimit = useLimit;
        this.customRuleType = customRuleType;
        this.orgId = orgId;
        this.appId = appId;
        this.serviceId = serviceId;
        this.apiId = apiId;
    }

    public int getUseLimit() {
        return useLimit;
    }

    public CustomRuleType getCustomRuleType() {
        return customRuleType;
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

    public UUID getApiId() {
        return apiId;
    }
}
