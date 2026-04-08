package com.coachcoach.user.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

public record NaverLoginRequest(
        @NotBlank
        String code,

        @NotBlank
        String state,

        @Nullable
        String fcmToken,

        @Nullable
        String deviceType,

        @Nullable
        String deviceId
) {
}
