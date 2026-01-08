package com.coachcoach.catalog.service.request;

import com.coachcoach.catalog.entity.enums.Unit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@ToString
public class IngredientCreateRequest {
    @NotBlank(message = "재료명 입력은 필수입니다.")
    private String ingredientName;
    @NotNull(message = "단위 입력은 필수입니다.")
    private Unit unit;
    @NotNull(message = "카테고리는 필수입니다.")
    private Long ingredientCategoryId;
    @NotNull(message = "가격 입력은 필수입니다.")
    private BigDecimal originalPrice;
    @NotNull(message = "사용량 입력은 필수입니다.")
    private BigDecimal originalAmount;
    private String supplier;
}