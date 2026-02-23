package com.coachcoach.user.dto.request;

public record NotificationTokenRequest(
        String token,
        String title,
        String body
) {
}
