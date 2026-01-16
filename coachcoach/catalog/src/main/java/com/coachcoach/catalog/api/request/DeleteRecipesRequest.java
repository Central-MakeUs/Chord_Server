package com.coachcoach.catalog.api.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
public class DeleteRecipesRequest {
    private List<Long> recipeIds;
}
