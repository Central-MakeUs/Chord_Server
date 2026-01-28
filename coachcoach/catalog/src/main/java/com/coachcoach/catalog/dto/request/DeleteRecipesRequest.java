package com.coachcoach.catalog.dto.request;

import java.util.List;

public record DeleteRecipesRequest (
        List<Long> recipeIds
) {}