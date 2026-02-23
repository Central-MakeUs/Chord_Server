package com.coachcoach.insight.exception;

import com.coachcoach.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum InsightErrorCode implements ErrorCode {
    // 회원가입
    NOTFOUND_USER("INSIGHT_001", "유저가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    NOTFOUND_STRATEGY("INSIGHT_002", "전략 카드가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    NOTFOUND_STRATEGY_TYPE("INSIGHT_003", "전략 유형이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    NOTFOUND_STRATEGY_BASELINE("INSIGHT_004", "전략 생성 기록이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    NOTFOUND_GUIDE_CODE("INSIGHT_005", "가이드 유형이 올바르지 않습니다.", HttpStatus.NOT_FOUND),
    NOTFOUND_MENU_SNAPSHOTS("INSIGHT_006", "메뉴 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND),

    STRATEGY_ALREADY_STARTED("INSIGHT_020", "이미 실행 중인 전략입니다.", HttpStatus.BAD_REQUEST),
    STRATEGY_ALREADY_COMPLETED("INSIGHT_021","이미 완료된 전략입니다.", HttpStatus.BAD_REQUEST),
    STRATEGY_NOT_STARTED("INSIGHT_022", "시작되지 않은 전략입니다.", HttpStatus.BAD_REQUEST),
    STRATEGY_MENU_NOT_FOUND("INSIGHT_023", "이미 삭제된 메뉴입니다.", HttpStatus.BAD_REQUEST),
    ;
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
