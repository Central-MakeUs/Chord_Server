package com.coachcoach.catalog.api.response;

import com.coachcoach.catalog.domain.entity.IngredientPriceHistory;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@ToString
@Getter
public class IngredientUpdateResponse {
    private BigDecimal unitPrice;
    private Integer baseQuantity;
    private String unitCode;
    private PriceHistoryResponse newHistory;

    public static IngredientUpdateResponse of(
            BigDecimal unitPrice,
            Integer baseQuantity,
            String unitCode,
            IngredientPriceHistory newHistory
    ) {
        IngredientUpdateResponse response = new IngredientUpdateResponse();

        response.unitPrice = unitPrice;
        response.baseQuantity = baseQuantity;
        response.unitCode = unitCode;
        response.newHistory = PriceHistoryResponse.of(
                newHistory,
                unitCode,
                baseQuantity
        );

        return response;
    }
}