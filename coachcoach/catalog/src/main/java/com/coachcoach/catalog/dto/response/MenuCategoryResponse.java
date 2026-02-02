package com.coachcoach.catalog.dto.response;

import com.coachcoach.catalog.domain.MenuCategory;


public record MenuCategoryResponse(
    String categoryCode,
    String categoryName,
    Integer displayOrder
) {
    public static MenuCategoryResponse from(MenuCategory mc) {
        return new MenuCategoryResponse(
                mc.getCategoryCode(),
                mc.getCategoryName(),
                mc.getDisplayOrder()
        );
    }
}
