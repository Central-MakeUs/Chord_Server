package com.coachcoach.catalog.service.response;

import com.coachcoach.catalog.entity.IngredientCategory;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class IngredientCategoryResponse {
    private Long categoryId;
    private Long userId;
    private String categoryName;

    public static IngredientCategoryResponse from(IngredientCategory ic) {
        IngredientCategoryResponse response = new IngredientCategoryResponse();

        response.categoryId = ic.getCategoryId();
        response.userId = ic.getUserId();
        response.categoryName = ic.getCategoryName();

        return response;
    }
}
