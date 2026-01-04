package com.coachcoach.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {
    // 공통 에러
    INVALID_INPUT_VALUE("COMMON_001", "입력값이 올바르지 않습니다", HttpStatus.BAD_REQUEST),
    METHOD_NOT_ALLOWED("COMMON_002", "지원하지 않는 HTTP 메서드입니다", HttpStatus.METHOD_NOT_ALLOWED),
    INTERNAL_SERVER_ERROR("COMMON_003", "서버 내부 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_TYPE_VALUE("COMMON_004", "잘못된 타입입니다", HttpStatus.BAD_REQUEST),
    MISSING_REQUEST_PARAMETER("COMMON_005", "필수 파라미터가 누락되었습니다", HttpStatus.BAD_REQUEST),

    // 인증/인가
    UNAUTHORIZED("AUTH_001", "인증이 필요합니다", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("AUTH_002", "접근 권한이 없습니다", HttpStatus.FORBIDDEN),
    INVALID_TOKEN("AUTH_003", "유효하지 않은 토큰입니다", HttpStatus.UNAUTHORIZED),
    ;

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
