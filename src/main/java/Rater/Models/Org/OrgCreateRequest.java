package Rater.Models.Org;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotBlank;

public class OrgCreateRequest {
    @NotBlank
    private String name;

    @JsonCreator
    public OrgCreateRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
