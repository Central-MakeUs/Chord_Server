package com.coachcoach.catalog.service.response;

import com.coachcoach.catalog.entity.Ingredient;
import com.coachcoach.catalog.entity.enums.Unit;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@ToString
public class IngredientResponse {
    private Long ingredientId;
    private String ingredientName;
    private String unit;
    private Integer baseQuantity;
    private BigDecimal currentUnitPrice;

    public static IngredientResponse from(Ingredient ingredient) {
        IngredientResponse response = new IngredientResponse();

        response.ingredientId = ingredient.getIngredientId();
        response.ingredientName = ingredient.getIngredientName();
        response.unit = ingredient.getUnit().name();
        response.baseQuantity = ingredient.getUnit().getBaseQuantity();
        response.currentUnitPrice = ingredient.getCurrentUnitPrice();

        return response;
    }
}
