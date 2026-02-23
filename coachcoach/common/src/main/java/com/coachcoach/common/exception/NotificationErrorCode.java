package com.coachcoach.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements ErrorCode {
    NOTIFICATION_UNAVAILABLE("NOTIFICATION_001", "알림을 이용할 수 없습니다.", HttpStatus.SERVICE_UNAVAILABLE),
    ;
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
