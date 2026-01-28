package com.coachcoach.catalog.dto.response;

import com.coachcoach.catalog.domain.IngredientPriceHistory;
import com.coachcoach.catalog.domain.Unit;

import java.math.BigDecimal;

public record IngredientUpdateResponse (
    BigDecimal unitPrice,
    Integer baseQuantity,
    String unitCode,
    PriceHistoryResponse newHistory
) {
    public static IngredientUpdateResponse of(
            BigDecimal unitPrice,
            Unit unit,
            IngredientPriceHistory newHistory
    ) {
        return new IngredientUpdateResponse(
                unitPrice,
                unit.getBaseQuantity(),
                unit.getUnitCode(),
                PriceHistoryResponse.of(newHistory, unit)
        );
    }
}