package com.coachcoach.user.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

public record AppleLoginRequest(
        @NotBlank
        String identityToken,

        @Nullable
        String fcmToken,

        @Nullable
        String deviceType,

        @Nullable
        String deviceId
) {
}
