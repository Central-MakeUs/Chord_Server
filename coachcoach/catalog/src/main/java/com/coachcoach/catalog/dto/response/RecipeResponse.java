package com.coachcoach.catalog.dto.response;


import java.math.BigDecimal;


public record RecipeResponse (
    Long recipeId,
    Long menuId,
    Long ingredientId,
    String ingredientName,
    BigDecimal amount,
    String unitCode,
    BigDecimal price
) { }