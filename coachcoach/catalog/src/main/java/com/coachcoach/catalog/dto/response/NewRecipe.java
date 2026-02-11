package com.coachcoach.catalog.dto.response;

import com.coachcoach.catalog.domain.Unit;
import com.coachcoach.catalog.dto.request.NewRecipeCreateRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;

/**
 * 메뉴 등록 - 새 재료 등록 시 VO
 * 리팩토링 필요
 */
@Builder
public record NewRecipe(
        BigDecimal amount,                  // 사용량 -> 구매량

        BigDecimal usageAmount,

        BigDecimal price,                   // 가격

        Unit unit,

        String ingredientCategoryCode,

        String ingredientName,

        String supplier,

        BigDecimal unitPrice
) {
    public NewRecipe withIngredientName(String ingredientName) {
        return new NewRecipe(
                this.amount,
                this.usageAmount,
                this.price,
                this.unit,
                this.ingredientCategoryCode,
                ingredientName,
                this.supplier,
                this.unitPrice
        );
    }
}
