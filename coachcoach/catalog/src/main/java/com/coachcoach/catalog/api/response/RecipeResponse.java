package com.coachcoach.catalog.api.response;

import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@ToString
public class RecipeResponse {
    private Long recipeId;
    private Long menuId;
    private Long ingredientId;
    private String ingredientName;
    private BigDecimal amount;
    private String unitCode;
    private BigDecimal price;

    public static RecipeResponse of(
            Long recipeId,
            Long menuId,
            Long ingredientId,
            String ingredientName,
            BigDecimal amount,
            String unitCode,
            BigDecimal price
    ) {
        RecipeResponse response = new RecipeResponse();

        response.recipeId = recipeId;
        response.menuId = menuId;
        response.ingredientId = ingredientId;
        response.ingredientName = ingredientName;
        response.amount = amount;
        response.unitCode = unitCode;
        response.price = price;

        return response;
    }
}