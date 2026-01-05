package com.coachcoach.catalog.service.response;

import com.coachcoach.catalog.entity.MenuCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class MenuCategoryResponse {
    private Long categoryId;
    private Long userId;
    private String categoryName;

    public static MenuCategoryResponse from(MenuCategory mc) {
        MenuCategoryResponse response = new MenuCategoryResponse();

        response.categoryId = mc.getCategoryId();
        response.userId = mc.getUserId();
        response.categoryName = mc.getCategoryName();

        return response;
    }
}
