package com.coachcoach.user.exception;

import com.coachcoach.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SocialLoginErrorCode implements ErrorCode {
    // 카카오 로그인
    KAKAO_BAD_REQUEST("SOCIAL_KAKAO_001", "카카오 로그인 요청이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    KAKAO_NOT_LINKED("SOCIAL_KAKAO_002", "카카오 계정 연결이 필요합니다. 다시 로그인해주세요.", HttpStatus.BAD_REQUEST),
    KAKAO_ALREADY_LINKED("SOCIAL_KAKAO_003", "이미 카카오 계정이 연결된 사용자입니다.", HttpStatus.BAD_REQUEST),
    KAKAO_INVALID_USER("SOCIAL_KAKAO_004", "존재하지 않거나 휴면 상태의 카카오 계정입니다.", HttpStatus.BAD_REQUEST),
    KAKAO_INVALID_PROPERTY("SOCIAL_KAKAO_005", "카카오 사용자 정보 요청 값이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    KAKAO_FORBIDDEN("SOCIAL_KAKAO_006", "필수 동의 항목이 누락되었습니다. 동의 후 다시 시도해주세요.", HttpStatus.FORBIDDEN),
    KAKAO_UNAUTHORIZED("SOCIAL_KAKAO_007", "카카오 계정 인증에 실패했습니다.", HttpStatus.UNAUTHORIZED),
    ;
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
