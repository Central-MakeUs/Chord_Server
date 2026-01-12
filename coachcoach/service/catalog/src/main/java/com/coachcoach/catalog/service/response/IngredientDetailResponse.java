package com.coachcoach.catalog.service.response;

import com.coachcoach.catalog.entity.Ingredient;
import com.coachcoach.catalog.entity.IngredientPriceHistory;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Getter
@ToString
public class IngredientDetailResponse {
    private Long ingredientId;
    private String ingredientName;      // 재료 이름
    private BigDecimal unitPrice;       // 단가
    private Integer baseQuantity;    // 단위 기준량
    private String unitCode;                // 단위
    private String supplier;            // 공급업체
    private List<String> menus;         // 해당 메뉴를 사용 중인 메뉴 목록
    private BigDecimal originalAmount;  // 구매량
    private BigDecimal originalPrice;   // 구매가
    private Boolean isFavorite;         // 즐겨찾기 여부

    public static IngredientDetailResponse of(Ingredient ingredient, Integer baseQuantity, List<String> menus, IngredientPriceHistory history) {
        IngredientDetailResponse response = new IngredientDetailResponse();

        response.ingredientId = ingredient.getIngredientId();
        response.ingredientName = ingredient.getIngredientName();
        response.unitPrice = ingredient.getCurrentUnitPrice();
        response.baseQuantity = baseQuantity;
        response.unitCode = ingredient.getUnitCode();
        response.supplier = ingredient.getSupplier();
        response.menus = menus;
        response.originalAmount = history.getOriginalAmount();
        response.originalPrice = history.getOriginalPrice();
        response.isFavorite = ingredient.getFavorite();

        return response;
    }
}