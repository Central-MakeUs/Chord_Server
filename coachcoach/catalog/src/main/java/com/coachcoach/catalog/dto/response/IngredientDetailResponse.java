package com.coachcoach.catalog.dto.response;

import com.coachcoach.catalog.domain.Ingredient;
import com.coachcoach.catalog.domain.IngredientPriceHistory;
import com.coachcoach.catalog.domain.Unit;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

public record IngredientDetailResponse (
        Long ingredientId,
        String ingredientName,      // 재료 이름
        BigDecimal unitPrice,       // 단가
        Integer baseQuantity,    // 단위 기준량
        String unitCode,                // 단위
        String supplier,            // 공급업체
        List<MenusInUse> menus,         // 해당 메뉴를 사용 중인 메뉴 목록
        BigDecimal originalAmount,  // 구매량(최근)
        BigDecimal originalPrice,   // 구매가(최근)
        Boolean isFavorite         // 즐겨찾기 여부
) {
    @Builder
    public IngredientDetailResponse{}

    public static IngredientDetailResponse of(
            Ingredient ingredient,
            Unit unit,
            List<MenusInUse> menus,
            IngredientPriceHistory history
    ) {
        return new IngredientDetailResponse(
                ingredient.getIngredientId(),
                ingredient.getIngredientName(),
                ingredient.getCurrentUnitPrice(),
                unit.getBaseQuantity(),
                unit.getUnitCode(),
                ingredient.getSupplier(),
                menus,
                history.getOriginalAmount(),
                history.getOriginalPrice(),
                ingredient.getFavorite()
        );
    }
}