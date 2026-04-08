package com.coachcoach.user.dto.request;

public record LogoutWithRefreshTokenRequest(
        String fcmToken,
        String refreshToken
) {
}
