package com.coachcoach.user.dto.request;

import com.coachcoach.user.dto.validation.ValidLoginId;
import com.coachcoach.user.dto.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SignUpRequest {
    @NotBlank(message = "아이디를 입력해주세요.")
    @ValidLoginId
    private String loginId;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @ValidPassword
    private String password;
}
