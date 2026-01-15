package com.coachcoach.catalog.global.exception;

import com.coachcoach.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CatalogErrorCode implements ErrorCode {
    DUP_CATEGORY("CATALOG_001", "이미 등록된 카테고리입니다", HttpStatus.CONFLICT),
    DUP_INGREDIENT("CATALOG_002", "이미 등록된 재료입니다", HttpStatus.CONFLICT),
    DUP_MENU("CATALOG_003", "이미 등록된 메뉴명입니다.", HttpStatus.CONFLICT),

    NOTFOUND_UNIT("CATALOG_004", "존재하지 않는 단위입니다.", HttpStatus.NOT_FOUND),
    NOTFOUND_CATEGORY("CATALOG_005", "존재하지 않는 카테고리입니다.", HttpStatus.NOT_FOUND),
    NOTFOUND_INGREDIENT("CATALOG_006", "존재하지 않는 재료입니다.", HttpStatus.NOT_FOUND),
    NOTFOUND_PRICEHISTORY("CATALOG_007", "변경 이력이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    NOTFOUND_TEMPLATE("CATALOG_008", "존재하지 않는 템플릿입니다.", HttpStatus.NOT_FOUND),
    NOTFOUND_MENU("CATALOG_009", "존재하지 않는 메뉴입니다.", HttpStatus.NOT_FOUND),
    NOTFOUND_MARGINGRADE("CATALOG_010", "존재하지 않는 마진 등급입니다.", HttpStatus.NOT_FOUND),

    INVALID_UNIT_PRICE("CATALOG_011", "단가는 0보다 커야 합니다.", HttpStatus.BAD_REQUEST),
    ;
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
