package com.coachcoach.catalog.dto.response;

import java.math.BigDecimal;

public record HomeMenusResponse(
        Long numOfDangerMenus,
        AvgCostRate avgCostRate,
        BigDecimal avgMarginRate
) {
}
