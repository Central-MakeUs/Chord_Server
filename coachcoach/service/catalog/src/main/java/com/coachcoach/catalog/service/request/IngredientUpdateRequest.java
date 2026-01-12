package com.coachcoach.catalog.service.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@ToString
@Getter
public class IngredientUpdateRequest {
    @NotNull(message = "가격 입력은 필수입니다.")
    private BigDecimal price;
    @NotNull(message = "사용량 입력은 필수입니다.")
    private BigDecimal amount;
    @NotBlank(message = "단위 입력은 필수입니다.")
    private String unitCode;
}