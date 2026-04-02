package com.coachcoach.user.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

public record FcmTokenRequest(
    String fcmToken,
    String deviceType,
    String deviceId
) {
}
