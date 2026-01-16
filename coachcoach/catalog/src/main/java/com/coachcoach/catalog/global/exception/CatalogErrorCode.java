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
    DUP_RECIPE("CATALOG_004", "이미 등록된 재료입니다.", HttpStatus.CONFLICT),

    NOTFOUND_UNIT("CATALOG_005", "존재하지 않는 단위입니다.", HttpStatus.NOT_FOUND),
    NOTFOUND_CATEGORY("CATALOG_006", "존재하지 않는 카테고리입니다.", HttpStatus.NOT_FOUND),
    NOTFOUND_INGREDIENT("CATALOG_007", "존재하지 않는 재료입니다.", HttpStatus.NOT_FOUND),
    NOTFOUND_PRICEHISTORY("CATALOG_008", "변경 이력이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    NOTFOUND_TEMPLATE("CATALOG_009", "존재하지 않는 템플릿입니다.", HttpStatus.NOT_FOUND),
    NOTFOUND_MENU("CATALOG_010", "존재하지 않는 메뉴입니다.", HttpStatus.NOT_FOUND),
    NOTFOUND_MARGINGRADE("CATALOG_011", "존재하지 않는 마진 등급입니다.", HttpStatus.NOT_FOUND),
    NOTFOUND_RECIPE("CATALOG_012", "등록되지 않은 재료입니다.", HttpStatus.NOT_FOUND),

    INVALID_UNIT_PRICE("CATALOG_012", "단가는 0보다 커야 합니다.", HttpStatus.BAD_REQUEST),
    INVALID_MENU_NAME("CATALOG_013", "메뉴명을 변경해주세요.", HttpStatus.BAD_REQUEST),
    INVALID_INGREDIENT_NAME("CATALOG_014", "재료명을 변경해주세요.", HttpStatus.BAD_REQUEST),
    ;
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
