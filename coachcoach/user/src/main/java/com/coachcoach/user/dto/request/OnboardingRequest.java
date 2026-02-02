package com.coachcoach.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record OnboardingRequest(
        @NotBlank(message = "매장명 입력은 필수입니다.")
        String name,

        @NotNull(message = "직원 수 입력은 필수입니다.")
        Integer employees,

        @NotNull(message = "시급 입력은 필수입니다.")
        BigDecimal laborCost,

        BigDecimal rentCost,

        Boolean includeWeeklyHolidayPay
) {
}
