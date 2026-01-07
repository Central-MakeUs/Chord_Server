package com.coachcoach.catalog.service.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class IngredientCategoryCreateRequest {
    @NotBlank(message = "카테고리는 필수입니다.")
    private String categoryName;
}
