package com.coachcoach.catalog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public record MenuCreateRequest (
    /* 메뉴 정보 */
    @NotBlank(message = "메뉴 카테고리 입력은 필수입니다.")
    String menuCategoryCode,

    @NotBlank(message = "메뉴명 입력은 필수입니다.")
    String menuName,

    @NotNull(message = "판매가 입력은 필수입니다.")
    @Positive(message = "판매가는 0보다 커야 합니다.")
    BigDecimal sellingPrice,

    @NotNull(message = "제조시간 입력은 필수입니다.")
    @Positive(message = "제조시간은 0보다 커야 합니다.")
    Integer workTime,

    /* 레시피 정보 */
    List<RecipeCreateRequest> recipes,          // 기존 재료로 레시피 등록
    List<NewRecipeCreateRequest> newRecipes    // 새로운 재료로 레시피 등록
) {
    public MenuCreateRequest {
        recipes = Objects.requireNonNullElse(recipes, Collections.emptyList());
        newRecipes = Objects.requireNonNullElse(newRecipes, Collections.emptyList());
    }
}
