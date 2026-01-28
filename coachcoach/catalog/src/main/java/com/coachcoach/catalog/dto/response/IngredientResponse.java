package com.coachcoach.catalog.dto.response;

import com.coachcoach.catalog.domain.Ingredient;
import com.coachcoach.catalog.domain.Unit;

import java.math.BigDecimal;


public record IngredientResponse(
    Long ingredientId,
    String ingredientCategoryCode,
    String ingredientName,
    String unitCode,
    Integer baseQuantity,
    BigDecimal currentUnitPrice
) {
    public static IngredientResponse of(
            Ingredient ingredient, Unit unit
    ) {
        return new IngredientResponse(
                ingredient.getIngredientId(),
                ingredient.getIngredientCategoryCode(),
                ingredient.getIngredientName(),
                unit.getUnitCode(),
                unit.getBaseQuantity(),
                ingredient.getCurrentUnitPrice()
        );
    }
}
