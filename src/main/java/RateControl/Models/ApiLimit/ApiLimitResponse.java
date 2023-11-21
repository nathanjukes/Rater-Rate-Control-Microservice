package RateControl.Models.ApiLimit;

public class ApiLimitResponse {
    private int useLimit;
    private CustomRuleType customRuleType;

    public ApiLimitResponse(int useLimit, CustomRuleType customRuleType) {
        this.useLimit = useLimit;
        this.customRuleType = customRuleType;
    }

    public int getUseLimit() {
        return useLimit;
    }

    public CustomRuleType getCustomRuleType() {
        return customRuleType;
    }
}
