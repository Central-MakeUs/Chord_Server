package com.coachcoach.catalog.api.response;

import com.coachcoach.catalog.domain.entity.Ingredient;
import com.coachcoach.catalog.domain.entity.TemplateIngredient;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SearchIngredientsResponse {
    private Boolean isTemplate;     // 템플릿 여부
    private Long templateId;        // isTemplate = true → templateId != null
    private Long ingredientId;      // isTempalte = true → ingredientId = null
    private String ingredientName;        // 메뉴명

    public static SearchIngredientsResponse from(Ingredient ingredient) {
        SearchIngredientsResponse response = new SearchIngredientsResponse();
        
        response.isTemplate = false;
        response.templateId = null;
        response.ingredientId = ingredient.getIngredientId();
        response.ingredientName = ingredient.getIngredientName();

        return response;
    }

    public static SearchIngredientsResponse from(TemplateIngredient ingredient) {
        SearchIngredientsResponse response = new SearchIngredientsResponse();

        response.isTemplate = true;
        response.templateId = ingredient.getIngredientTemplateId();
        response.ingredientId = null;
        response.ingredientName = ingredient.getIngredientName();

        return response;
    }
}
