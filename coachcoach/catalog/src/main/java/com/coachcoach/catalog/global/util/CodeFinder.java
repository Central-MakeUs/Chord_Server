package com.coachcoach.catalog.global.util;

import com.coachcoach.catalog.domain.entity.IngredientCategory;
import com.coachcoach.catalog.domain.entity.MenuCategory;
import com.coachcoach.catalog.domain.entity.Unit;
import com.coachcoach.catalog.global.exception.CatalogErrorCode;
import com.coachcoach.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CodeFinder {
    private final Cache cache;

    /**
     * 재료 카테고리 코드로 카테고리 찾기
     */
    public IngredientCategory findIngredientCategoryByCode(String categoryCode) {
        return cache.getIngredientCategories().stream()
                .filter(category -> category.getCategoryCode().equals(categoryCode))
                .findFirst()
                .orElseThrow(() -> new BusinessException(CatalogErrorCode.NOTFOUND_CATEGORY));
    }

    public boolean existsIngredientCategory(String categoryCode) {
        return cache.getIngredientCategories().stream()
                .anyMatch(category -> category.getCategoryCode().equals(categoryCode));
    }

    /**
     * 메뉴 카테고리 코드로 카테고리 찾기
     */
    public MenuCategory findMenuCategoryByCode(String categoryCode) {
        return cache.getMenuCategories().stream()
                .filter(category -> category.getCategoryCode().equals(categoryCode))
                .findFirst()
                .orElseThrow(() -> new BusinessException(CatalogErrorCode.NOTFOUND_CATEGORY));
    }

    public boolean existsMenuCategory(String categoryCode) {
        return cache.getMenuCategories().stream()
                .anyMatch(category -> category.getCategoryCode().equals(categoryCode));
    }

    /**
     * 단위 코드로 단위 찾기
     */
    public Unit findUnitByCode(String unitCode) {
        return cache.getUnits().stream()
                .filter(unit -> unit.getUnitCode().equals(unitCode))
                .findFirst()
                .orElseThrow(() -> new BusinessException(CatalogErrorCode.NOTFOUND_UNIT));
    }

    public boolean existsUnit(String unitCode) {
        return cache.getUnits().stream()
                .anyMatch(unit -> unit.getUnitCode().equals(unitCode));
    }
}
