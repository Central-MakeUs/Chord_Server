package com.coachcoach.user.dto.request;

import com.coachcoach.user.dto.validation.ValidLoginId;
import com.coachcoach.user.dto.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;

public record SignUpRequest (
    @NotBlank(message = "아이디를 입력해주세요.")
    @ValidLoginId
    String loginId,

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @ValidPassword
    String password
) {
}