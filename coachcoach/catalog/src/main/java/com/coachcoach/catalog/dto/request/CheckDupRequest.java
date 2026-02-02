package com.coachcoach.catalog.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public record CheckDupRequest (
        @NotBlank(message = "메뉴명 입력은 필수입니다.")
        String menuName,

        List<String> ingredientNames
) {
        public CheckDupRequest{
                ingredientNames = Objects.requireNonNullElse(ingredientNames, Collections.emptyList());
        }
}