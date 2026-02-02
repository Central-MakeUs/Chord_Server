package com.coachcoach.catalog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;

public record IngredientCreateRequest (
        @NotBlank(message = "카테고리 입력은 필수입니다.")
        String categoryCode,        // INGREDIENTS / MATERIAL

        @NotBlank(message = "재료명 입력은 필수입니다.")
        String ingredientName,

        @NotBlank(message = "단위 입력은 필수입니다.")
        String unitCode,            // G / KG / EA / ML

        @NotNull(message = "가격 입력은 필수입니다.")
        @Positive(message = "가격은 0보다 커야 합니다.")
        BigDecimal price,

        @NotNull(message = "사용량 입력은 필수입니다.")
        @Positive(message = "사용량은 0보다 커야 합니다.")
        BigDecimal amount,

        String supplier
) {}
