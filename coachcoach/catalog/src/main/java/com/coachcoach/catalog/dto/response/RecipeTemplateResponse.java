package com.coachcoach.catalog.dto.response;

import com.coachcoach.catalog.domain.TemplateIngredient;
import com.coachcoach.catalog.domain.TemplateRecipe;
import com.coachcoach.catalog.domain.Unit;

import java.math.BigDecimal;

public record RecipeTemplateResponse (
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