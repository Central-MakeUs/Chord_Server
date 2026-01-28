package com.coachcoach.catalog.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@ToString
public class AmountUpdateRequest {
    @NotNull(message = "사용량 입력은 필수입니다.")
    @Positive(message = "사용량은 0보다 커야 합니다.")
    private BigDecimal amount;      // 사용량
}
