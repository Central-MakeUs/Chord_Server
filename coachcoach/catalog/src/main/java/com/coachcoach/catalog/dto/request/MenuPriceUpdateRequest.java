package com.coachcoach.catalog.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record MenuPriceUpdateRequest (
    @NotNull(message = "판매가 입력은 필수입니다.")
    @Positive(message = "판매가는 0보다 커야 합니다.")
    BigDecimal sellingPrice
) {}