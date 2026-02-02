package com.coachcoach.common.dto.internal;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserInfo(
        Long userId,
        String loginId,
        LocalDateTime lastLoginAt,
        Boolean onboardingCompleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
