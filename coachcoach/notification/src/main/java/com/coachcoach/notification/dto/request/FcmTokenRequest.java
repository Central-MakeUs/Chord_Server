package com.coachcoach.notification.dto.request;

public record FcmTokenRequest(
    String fcmToken,
    String deviceType,
    String deviceId
) {
}
