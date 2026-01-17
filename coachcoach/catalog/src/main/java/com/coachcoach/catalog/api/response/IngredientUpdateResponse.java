package com.coachcoach.catalog.api.response;

import com.coachcoach.catalog.domain.entity.IngredientPriceHistory;
import com.coachcoach.catalog.domain.entity.Unit;
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
            Unit unit,
            IngredientPriceHistory newHistory
    ) {
        IngredientUpdateResponse response = new IngredientUpdateResponse();

        response.unitPrice = unitPrice;
        response.baseQuantity = unit.getBaseQuantity();
        response.unitCode = unit.getUnitCode();
        response.newHistory = PriceHistoryResponse.of(
                newHistory,
                unit
        );

        return response;
    }
}