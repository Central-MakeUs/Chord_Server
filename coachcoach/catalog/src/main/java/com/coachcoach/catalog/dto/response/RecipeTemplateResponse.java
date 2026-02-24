package com.coachcoach.catalog.dto.response;

import com.coachcoach.catalog.domain.TemplateIngredient;
import com.coachcoach.catalog.domain.TemplateRecipe;
import com.coachcoach.catalog.domain.Unit;
import jakarta.annotation.Nullable;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record RecipeTemplateResponse (
    @Nullable
    Long ingredientId,      // 사용자에게 이미 재료가 존재하는 경우에만 적용
    String ingredientName,
    BigDecimal defaultUsageAmount,
    BigDecimal defaultPrice,
    BigDecimal unitPrice,
    Integer baseQuantity,
    String unitCode,
    String ingredientCategoryCode
) {
    public static RecipeTemplateResponse of(
            TemplateRecipe recipe,
            TemplateIngredient ingredient,
            Unit unit
    ) {
        return new RecipeTemplateResponse(
            null,
            ingredient.getIngredientName(),
            recipe.getDefaultUsageAmount(),
            recipe.getDefaultCost(),
            ingredient.getDefaultUnitPrice(),
            unit.getBaseQuantity(),
            unit.getUnitCode(),
            ingredient.getIngredientCategoryCode()
        );
    }
}