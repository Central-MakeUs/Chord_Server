package com.coachcoach.catalog.dto.response;

import com.coachcoach.catalog.domain.TemplateIngredient;
import com.coachcoach.catalog.domain.TemplateRecipe;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@ToString
public class RecipeTemplateResponse {
    private String ingredientName;
    private BigDecimal defaultUsageAmount;
    private BigDecimal defaultPrice;
    private String unitCode;

    public static RecipeTemplateResponse of(
            TemplateRecipe recipe,
            TemplateIngredient ingredient
    ) {
        RecipeTemplateResponse response = new RecipeTemplateResponse();

        response.ingredientName = ingredient.getIngredientName();
        response.defaultUsageAmount = recipe.getDefaultUsageAmount();
        response.defaultPrice = recipe.getDefaultPrice();
        response.unitCode = ingredient.getUnitCode();

        return response;
    }
}