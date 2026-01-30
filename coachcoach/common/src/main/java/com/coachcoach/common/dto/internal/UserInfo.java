package com.coachcoach.common.dto.internal;

import java.time.LocalDateTime;

public record UserInfo(
        Long userId,
        String loginId,
        LocalDateTime lastLoginAt,
        Boolean onboardingCompleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
