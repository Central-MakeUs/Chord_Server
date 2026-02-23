package com.coachcoach.user.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest (
    @NotBlank(message = "아이디를 입력해주세요.")
    String loginId,

    @NotBlank(message = "비밀번호를 입력해주세요.")
    String password,

    @Nullable
    String fcmToken,

    @Nullable
    String deviceType,

    @Nullable
    String deviceId
) {
}
