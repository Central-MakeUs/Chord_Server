package com.coachcoach.catalog.dto.response;

import com.coachcoach.catalog.domain.IngredientCategory;


public record IngredientCategoryResponse(
    String categoryCode,
    String categoryName,
    Integer displayOrder
) {
    public static IngredientCategoryResponse from(IngredientCategory category) {
        return new IngredientCategoryResponse(
                category.getCategoryCode(),
                category.getCategoryName(),
                category.getDisplayOrder()
        );
    }
}
