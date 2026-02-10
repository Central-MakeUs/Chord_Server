package com.coachcoach.catalog.dto.request;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public record DeleteRecipesRequest (
        List<Long> recipeIds
) {
    public DeleteRecipesRequest {
        recipeIds = Objects.requireNonNullElse(recipeIds, Collections.emptyList());
    }
}