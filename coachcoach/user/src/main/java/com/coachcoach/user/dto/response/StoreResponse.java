package com.coachcoach.user.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record StoreResponse(
        String name,
        Integer employees,
        BigDecimal laborCost,
        BigDecimal rentCost,
        Boolean includeWeeklyHolidayPay
) {
}
