package com.coachcoach.catalog.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@ToString
public class IngredientCreateRequest {
    @NotBlank(message = "카테고리 입력은 필수입니다.")
    private String categoryCode;        // INGREDIENTS / MATERIAL
    @NotBlank(message = "재료명 입력은 필수입니다.")
    private String ingredientName;
    @NotBlank(message = "단위 입력은 필수입니다.")
    private String unitCode;            // G / KG / EA / ML
    @NotNull(message = "가격 입력은 필수입니다.")
    private BigDecimal price;
    @NotNull(message = "사용량 입력은 필수입니다.")
    private BigDecimal amount;
    private String supplier;
}
