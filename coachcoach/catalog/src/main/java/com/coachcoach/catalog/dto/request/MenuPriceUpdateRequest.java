package com.coachcoach.catalog.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@ToString
public class MenuPriceUpdateRequest {
    @NotNull(message = "판매가 입력은 필수입니다.")
    @Positive(message = "판매가는 0보다 커야 합니다.")
    private BigDecimal sellingPrice;
}
