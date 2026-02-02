package com.coachcoach.catalog.dto.response;

import com.coachcoach.catalog.domain.Ingredient;

public record SearchMyIngredientsResponse (
    Long ingredientId,
    String ingredientName
) {

    public static SearchMyIngredientsResponse from(Ingredient ingredient) {
        return new SearchMyIngredientsResponse(
            ingredient.getIngredientId(),
            ingredient.getIngredientName()
        );
    }
}
