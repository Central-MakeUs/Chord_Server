package com.coachcoach.catalog.dto.response;


import java.math.BigDecimal;
import java.util.List;

public record RecipeListResponse (
    List<RecipeResponse> recipes,
    BigDecimal totalCost
) { }
