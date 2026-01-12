package com.coachcoach.catalog.service.response;

import com.coachcoach.catalog.entity.IngredientCategory;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

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
