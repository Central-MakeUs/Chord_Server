package com.coachcoach.user.dto.request;

import jakarta.annotation.Nullable;

public record DeleteUserRequest(
        @Nullable
        String accessToken
) {
}
