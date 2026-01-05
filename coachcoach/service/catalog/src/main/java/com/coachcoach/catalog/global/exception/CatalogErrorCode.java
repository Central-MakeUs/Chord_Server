package com.coachcoach.catalog.global.exception;

import com.coachcoach.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CatalogErrorCode implements ErrorCode {
    UNAUTHORIZED("AUTH_001", "인증이 필요합니다", HttpStatus.UNAUTHORIZED),
    ;
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
