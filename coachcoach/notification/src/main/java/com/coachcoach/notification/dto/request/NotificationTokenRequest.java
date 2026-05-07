package com.coachcoach.notification.dto.request;

public record NotificationTokenRequest(
        String token,
        String title,
        String body
) {
}
