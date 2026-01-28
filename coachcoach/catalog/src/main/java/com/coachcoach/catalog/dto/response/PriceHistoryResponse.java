package com.coachcoach.catalog.dto.response;

import com.coachcoach.catalog.domain.IngredientPriceHistory;
import com.coachcoach.catalog.domain.Unit;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PriceHistoryResponse(
    Long historyId,
    LocalDateTime changeDate,
    BigDecimal unitPrice,
    String unitCode,
    Integer baseQuantity
) {
    public static PriceHistoryResponse of(
            IngredientPriceHistory history, Unit unit
    ) {
        return new PriceHistoryResponse(
            history.getHistoryId(),
            history.getCreatedAt(),
            history.getUnitPrice(),
            unit.getUnitCode(),
            unit.getBaseQuantity()
        );
    }
}
