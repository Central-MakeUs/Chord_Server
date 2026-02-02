package com.coachcoach.user.dto.response;

public record LoginResponse (
    String accessToken,
    String refreshToken,
    Boolean onboardingCompleted
) {
}
