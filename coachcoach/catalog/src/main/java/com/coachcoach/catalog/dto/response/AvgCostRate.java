package com.coachcoach.catalog.dto.response;

import java.math.BigDecimal;

public record AvgCostRate(
        BigDecimal avgCostRate,
        String marginGradeCode
) {
}
