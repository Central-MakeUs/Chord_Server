package com.coachcoach.catalog.service.response;

import jakarta.persistence.Column;

import java.math.BigDecimal;

public class IngredientResponse {
    private Long ingredientId;
    private String ingredientCategoryCode;
    private String ingredientName;
    private String unitCode;
    private String baseQuantity;
    private BigDecimal currentUnitPrice;
}
