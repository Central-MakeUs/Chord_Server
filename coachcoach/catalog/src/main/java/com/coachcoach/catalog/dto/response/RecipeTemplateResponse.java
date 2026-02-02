package com.coachcoach.catalog.dto.response;

import com.coachcoach.catalog.domain.TemplateIngredient;
import com.coachcoach.catalog.domain.TemplateRecipe;

import java.math.BigDecimal;

public record RecipeTemplateResponse (
    String ingredientName,
    BigDecimal defaultUsageAmount,
    BigDecimal defaultPrice,
    String unitCode
) {
    public static RecipeTemplateResponse of(
            TemplateRecipe recipe,
            TemplateIngredient ingredient
    ) {
        return new RecipeTemplateResponse(
            ingredient.getIngredientName(),
            recipe.getDefaultUsageAmount(),
            recipe.getDefaultCost(),
            ingredient.getUnitCode()
        );
    }
}