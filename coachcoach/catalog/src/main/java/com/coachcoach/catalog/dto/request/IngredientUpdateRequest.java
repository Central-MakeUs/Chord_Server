package com.coachcoach.catalog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@ToString
@Getter
public class IngredientUpdateRequest {
    @NotBlank(message = "카테고리 입력은 필수입니다.")
    private String category;
    @NotNull(message = "가격 입력은 필수입니다.")
    @Positive(message = "가격은 0원보다 커야 합니다.")
    private BigDecimal price;
    @NotNull(message = "사용량 입력은 필수입니다.")
    @Positive(message = "사용량은 0보다 커야 합니다.")
    private BigDecimal amount;
    @NotBlank(message = "단위 입력은 필수입니다.")
    private String unitCode;
}