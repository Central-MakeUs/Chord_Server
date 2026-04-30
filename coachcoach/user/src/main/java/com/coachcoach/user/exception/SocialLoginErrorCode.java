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
    NAVER_INVALID_REQUEST("SOCIAL_NAVER_002", "필수 요청 값이 누락되었거나 잘못되었습니다.", HttpStatus.BAD_REQUEST),
    NAVER_UNAUTHORIZED("SOCIAL_NAVER_003", "네이버 인증에 실패했습니다.", HttpStatus.UNAUTHORIZED),
    NAVER_FORBIDDEN("SOCIAL_NAVER_005", "허용되지 않은 접근입니다.", HttpStatus.FORBIDDEN),
    NAVER_NOT_FOUND("SOCIAL_NAVER_006", "요청한 네이버 API를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    NAVER_SERVER_ERROR("SOCIAL_NAVER_009", "네이버 서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    // 애플 로그인
    APPLE_UNAUTHORIZED("SOCIAL_APPLE_001", "애플 계정 인증에 실패했습니다.", HttpStatus.UNAUTHORIZED),
    APPLE_INVALID_TOKEN("SOCIAL_APPLE_002", "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
    APPLE_EXPIRED_TOKEN("SOCIAL_APPLE_003", "토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    APPLE_INVALID_ISSUER("SOCIAL_APPLE_004", "토큰의 발급자가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),
    APPLE_INVALID_AUDIENCE("SOCIAL_APPLE_005", "토큰의 대상이 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),
    APPLE_PUBLIC_KEY_NOT_FOUND("SOCIAL_APPLE_006", "매칭되는 애플 공개키를 찾을 수 없습니다.", HttpStatus.UNAUTHORIZED),
    APPLE_PUBLIC_KEY_FETCH_FAILED("SOCIAL_APPLE_007", "애플 공개키 조회에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    APPLE_PUBLIC_KEY_INVALID("SOCIAL_APPLE_008", "애플 공개키 생성에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    APPLE_NOT_LINKED("SOCIAL_APPLE_009", "애플 계정 연결이 필요합니다. 다시 로그인해주세요.", HttpStatus.BAD_REQUEST),
    APPLE_ALREADY_LINKED("SOCIAL_APPLE_010", "이미 애플 계정이 연결된 사용자입니다.", HttpStatus.BAD_REQUEST),
    APPLE_FORBIDDEN("SOCIAL_APPLE_011", "필수 동의 항목이 누락되었습니다. 동의 후 다시 시도해주세요.", HttpStatus.FORBIDDEN),
    ;
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
