package com.coachcoach.catalog.service.response;

import com.coachcoach.catalog.entity.Ingredient;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@ToString
public class IngredientResponse {
    private Long ingredientId;
    private String ingredientCategoryCode;
    private String ingredientName;
    private String unitCode;
    private Integer baseQuantity;
    private BigDecimal currentUnitPrice;

    public static IngredientResponse of(Ingredient ingredient, Integer baseQuantity) {
        IngredientResponse response = new IngredientResponse();

        response.ingredientId = ingredient.getIngredientId();
        response.ingredientCategoryCode = ingredient.getIngredientCategoryCode();
        response.ingredientName = ingredient.getIngredientName();
        response.unitCode = ingredient.getUnitCode();
        response.baseQuantity = baseQuantity;
        response.currentUnitPrice = ingredient.getCurrentUnitPrice();

        return response;
    }
}
