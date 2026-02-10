package com.coachcoach.catalog.dto.response;

import java.math.BigDecimal;

public record HomeMenusResponse(
        int numOfDangerMenus,
        AvgCostRate avgCostRate,
        BigDecimal avgMarginRate
) {
}
