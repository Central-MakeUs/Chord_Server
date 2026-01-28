package com.coachcoach.catalog.dto.response;

import com.coachcoach.catalog.domain.IngredientCategory;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class IngredientCategoryResponse {
    private String categoryCode;
    private String categoryName;
    private Integer displayOrder;

    public static IngredientCategoryResponse from(IngredientCategory ic) {
        IngredientCategoryResponse response = new IngredientCategoryResponse();

        response.categoryCode = ic.getCategoryCode();
        response.categoryName = ic.getCategoryName();
        response.displayOrder = ic.getDisplayOrder();

        return response;
    }
}
