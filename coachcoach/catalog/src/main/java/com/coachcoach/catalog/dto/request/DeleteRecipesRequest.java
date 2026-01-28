package com.coachcoach.catalog.dto.request;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
public class DeleteRecipesRequest {
    private List<Long> recipeIds;
}
