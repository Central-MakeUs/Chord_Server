package com.coachcoach.catalog.api.response;

import com.coachcoach.catalog.domain.entity.Ingredient;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SearchMyIngredientsResponse {
    private Long ingredientId;
    private String ingredientName;

    public static SearchMyIngredientsResponse from(Ingredient ingredient) {
        SearchMyIngredientsResponse response = new SearchMyIngredientsResponse();

        response.ingredientId = ingredient.getIngredientId();
        response.ingredientName = ingredient.getIngredientName();

        return response;
    }
}
