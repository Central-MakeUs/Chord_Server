package com.coachcoach.catalog.dto.response;

import com.coachcoach.catalog.domain.MenuCategory;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MenuCategoryResponse {
    private String categoryCode;
    private String categoryName;
    private Integer displayOrder;

    public static MenuCategoryResponse from(MenuCategory mc) {
        MenuCategoryResponse response = new MenuCategoryResponse();

        response.categoryCode = mc.getCategoryCode();
        response.categoryName = mc.getCategoryName();
        response.displayOrder = mc.getDisplayOrder();

        return response;
    }
}
