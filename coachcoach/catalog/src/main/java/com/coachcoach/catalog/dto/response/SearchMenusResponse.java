package com.coachcoach.catalog.dto.response;

import com.coachcoach.catalog.domain.TemplateMenu;

public record SearchMenusResponse (
    Long templateId,
    String menuName
) {
    public static SearchMenusResponse from(TemplateMenu templateMenu) {
        return new SearchMenusResponse(
            templateMenu.getTemplateId(),
            templateMenu.getMenuName()
        );
    }
}
