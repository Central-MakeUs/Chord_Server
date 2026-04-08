package com.coachcoach.user.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

public record KakaoLoginRequest(
        @NotBlank
        String accessToken,

        @Nullable
        String fcmToken,

        @Nullable
        String deviceType,

        @Nullable
        String deviceId
) {
}
