package com.coachcoach.catalog.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

/*
    새로운 재료 등록 & 레시피 등록
 */
@Getter
@ToString
public class NewRecipeCreateRequest {
    @NotNull(message = "사용량 입력은 필수입니다.")
    @Positive(message = "사용량은 0보다 커야 합니다.")
    private BigDecimal amount;                  // 사용량
    @NotNull(message = "가격 입력은 필수입니다.")
    @Positive(message = "가격은 0보다 커야 합니다.")
    private BigDecimal price;                   // 가격
    @NotBlank(message = "단위 입력은 필수입니다.")
    private String unitCode;

    @NotBlank(message = "재료 카테고리 입력은 필수입니다.")
    private String ingredientCategoryCode;
    @NotBlank(message = "재료명 입력은 필수입니다.")
    private String ingredientName;
    private String supplier;

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }
}
