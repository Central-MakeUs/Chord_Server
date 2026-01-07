package com.coachcoach.catalog.global.exception;

import com.coachcoach.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CatalogErrorCode implements ErrorCode {
    DUPCATEGORY("CATALOG_001", "이미 등록된 카테고리입니다", HttpStatus.CONFLICT),
    ;
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
