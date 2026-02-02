package com.coachcoach.catalog.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/*
    기존 재료로 레시피 등록
 */
public record RecipeCreateRequest (
    Long ingredientId,

    @NotNull(message = "사용량 입력은 필수입니다.")
    @Positive(message = "사용량은 0보다 커야 합니다.")
    BigDecimal amount
) {}
