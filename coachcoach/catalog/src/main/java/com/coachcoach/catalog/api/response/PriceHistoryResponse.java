package com.coachcoach.catalog.api.response;

import com.coachcoach.catalog.domain.entity.IngredientPriceHistory;
import com.coachcoach.catalog.domain.entity.Unit;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ToString
@Getter
public class PriceHistoryResponse {
    private Long historyId;
    private LocalDateTime changeDate;
    private BigDecimal unitPrice;
    private String unitCode;
    private Integer baseQuantity;

    public static PriceHistoryResponse of(
            IngredientPriceHistory history, Unit unit
    ) {
        PriceHistoryResponse priceHistoryResponse = new PriceHistoryResponse();

        priceHistoryResponse.historyId = history.getHistoryId();
        priceHistoryResponse.changeDate = history.getCreatedAt();
        priceHistoryResponse.unitPrice = history.getUnitPrice();
        priceHistoryResponse.unitCode = unit.getUnitCode();
        priceHistoryResponse.baseQuantity = unit.getBaseQuantity();

        return priceHistoryResponse;
    }
}
