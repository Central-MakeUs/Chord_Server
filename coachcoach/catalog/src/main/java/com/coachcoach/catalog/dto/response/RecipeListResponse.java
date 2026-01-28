package com.coachcoach.catalog.dto.response;

import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Getter
@ToString
public class RecipeListResponse {
    private List<RecipeResponse> recipes;
    private BigDecimal totalCost;

    public static RecipeListResponse of(
            List<RecipeResponse> recipes,
            BigDecimal totalCost
    ) {
        RecipeListResponse recipeListResponse = new RecipeListResponse();
        recipeListResponse.recipes = recipes;
        recipeListResponse.totalCost = totalCost;

        return recipeListResponse;
    }
}
