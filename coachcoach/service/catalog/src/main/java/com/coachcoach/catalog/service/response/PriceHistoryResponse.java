package com.coachcoach.catalog.service.response;

import com.coachcoach.catalog.entity.IngredientPriceHistory;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ToString
@Getter
public class PriceHistoryResponse {
    private LocalDateTime changeDate;
    private BigDecimal unitPrice;
    private String unit;
    private Integer baseQuantity;

    public static PriceHistoryResponse of(IngredientPriceHistory history, String unitCode, Integer baseQuantity) {
        PriceHistoryResponse priceHistoryResponse = new PriceHistoryResponse();

        priceHistoryResponse.changeDate = history.getCreatedAt();
        priceHistoryResponse.unitPrice = history.getUnitPrice();
        priceHistoryResponse.unit = unitCode;
        priceHistoryResponse.baseQuantity = baseQuantity;

        return priceHistoryResponse;
    }
}
