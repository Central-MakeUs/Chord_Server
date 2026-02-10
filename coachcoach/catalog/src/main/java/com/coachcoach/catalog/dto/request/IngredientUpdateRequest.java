package com.coachcoach.catalog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record IngredientUpdateRequest (
    @NotBlank(message = "카테고리 입력은 필수입니다.")
    String category,

    @NotNull(message = "가격 입력은 필수입니다.")
    BigDecimal price,

    @NotNull(message = "사용량 입력은 필수입니다.")
    @Positive(message = "사용량은 0보다 커야 합니다.")
    BigDecimal amount,

    @NotBlank(message = "단위 입력은 필수입니다.")
    String unitCode
) {}