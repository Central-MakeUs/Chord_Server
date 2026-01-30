package com.coachcoach.user.exception;

import com.coachcoach.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    // 회원가입
    DUP_LOGIN_ID("USER_001", "이미 사용 중인 아이디입니다.", HttpStatus.CONFLICT),
    PASSWORD_CONTAINS_LOGIN_ID("USER_020", "비밀번호에 아이디가 포함될 수 없습니다.", HttpStatus.BAD_REQUEST),

    // 로그인
    NOTFOUND_LOGIN_ID("USER_010", "존재하지 않는 아이디입니다.", HttpStatus.UNAUTHORIZED),
    INVALID_PASSWORD("USER_021", "비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),

    NOTFOUND_STORE("USER_011", "스토어가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    NOTFOUND_USER("USER_012", "유저가 존재하지 않습니다.", HttpStatus.NOT_FOUND)
    ;
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
