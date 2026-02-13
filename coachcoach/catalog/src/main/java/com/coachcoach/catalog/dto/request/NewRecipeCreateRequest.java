package com.coachcoach.catalog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/*
    새로운 재료 등록 & 레시피 등록
 */
public record NewRecipeCreateRequest (
    @NotNull(message = "구매량 입력은 필수입니다.")
    @Positive(message = "구매량은 0보다 커야 합니다.")
    BigDecimal amount,                  // 사용량 -> 구매량

    @NotNull(message = "사용량 입력은 필수입니다.")
    @Positive(message = "사용량은 0보다 커야 합니다.")
    BigDecimal usageAmount,

    @NotNull(message = "가격 입력은 필수입니다.")
    BigDecimal price,                   // 가격

    @NotBlank(message = "단위 입력은 필수입니다.")
    String unitCode,

    @NotBlank(message = "재료 카테고리 입력은 필수입니다.")
    String ingredientCategoryCode,

    @NotBlank(message = "재료명 입력은 필수입니다.")
    String ingredientName,

    String supplier
) {
}