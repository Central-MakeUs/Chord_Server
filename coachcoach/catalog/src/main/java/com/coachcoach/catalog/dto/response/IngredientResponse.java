package com.coachcoach.catalog.dto.response;

import com.coachcoach.catalog.domain.Ingredient;
import com.coachcoach.catalog.domain.Unit;
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

    public static IngredientResponse of(
            Ingredient ingredient, Unit unit
    ) {
        IngredientResponse response = new IngredientResponse();

        response.ingredientId = ingredient.getIngredientId();
        response.ingredientCategoryCode = ingredient.getIngredientCategoryCode();
        response.ingredientName = ingredient.getIngredientName();
        response.unitCode = unit.getUnitCode();
        response.baseQuantity = unit.getBaseQuantity();
        response.currentUnitPrice = ingredient.getCurrentUnitPrice();

        return response;
    }
}
