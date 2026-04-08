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

    // 네이버 로그인
    // 네이버 로그인

    NAVER_BAD_REQUEST("SOCIAL_NAVER_001", "네이버 로그인 요청 값이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    NAVER_INVALID_REQUEST("SOCIAL_NAVER_002", "필수 요청 값이 누락되었거나 잘못되었습니다.", HttpStatus.BAD_REQUEST),
    NAVER_UNAUTHORIZED("SOCIAL_NAVER_003", "네이버 인증에 실패했습니다.", HttpStatus.UNAUTHORIZED),
    NAVER_INVALID_TOKEN("SOCIAL_NAVER_004", "유효하지 않거나 만료된 네이버 토큰입니다.", HttpStatus.UNAUTHORIZED),
    NAVER_FORBIDDEN("SOCIAL_NAVER_005", "허용되지 않은 접근입니다.", HttpStatus.FORBIDDEN),
    NAVER_NOT_FOUND("SOCIAL_NAVER_006", "요청한 네이버 API를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    NAVER_METHOD_NOT_ALLOWED("SOCIAL_NAVER_007", "잘못된 HTTP 메서드 요청입니다.", HttpStatus.METHOD_NOT_ALLOWED),
    NAVER_TOO_MANY_REQUESTS("SOCIAL_NAVER_008", "네이버 API 호출 한도를 초과했습니다.", HttpStatus.TOO_MANY_REQUESTS),
    NAVER_SERVER_ERROR("SOCIAL_NAVER_009", "네이버 서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    ;
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
