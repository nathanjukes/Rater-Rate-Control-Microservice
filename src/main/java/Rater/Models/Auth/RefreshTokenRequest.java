package Rater.Models.Auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class RefreshTokenRequest {
    @NotNull
    private UUID refreshToken;

    @JsonCreator
    public RefreshTokenRequest(UUID refreshToken) {
        this.refreshToken = refreshToken;
    }

    public UUID getRefreshToken() {
        return this.refreshToken;
    }
}
