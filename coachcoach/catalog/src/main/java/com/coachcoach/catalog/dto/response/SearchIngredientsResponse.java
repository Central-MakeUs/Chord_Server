package com.coachcoach.catalog.dto.response;

import com.coachcoach.catalog.domain.Ingredient;
import com.coachcoach.catalog.domain.TemplateIngredient;


public record SearchIngredientsResponse (
    Boolean isTemplate,     // 템플릿 여부
    Long templateId,        // isTemplate = true → templateId != null
    Long ingredientId,      // isTempalte = true → ingredientId = null
    String ingredientName        // 메뉴명
) {
    public static SearchIngredientsResponse from(Ingredient ingredient) {
        return new SearchIngredientsResponse(
            false,
            null,
            ingredient.getIngredientId(),
            ingredient.getIngredientName()
        );
    }

    public static SearchIngredientsResponse from(TemplateIngredient ingredient) {
        return new SearchIngredientsResponse(
            true,
            ingredient.getIngredientTemplateId(),
            null,
            ingredient.getIngredientName()
        );
    }
}
