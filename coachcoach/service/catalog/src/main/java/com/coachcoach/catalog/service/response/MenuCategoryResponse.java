package com.coachcoach.catalog.service.response;

import com.coachcoach.catalog.entity.MenuCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

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
