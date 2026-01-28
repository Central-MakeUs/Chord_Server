package com.coachcoach.catalog.dto.response;

import com.coachcoach.catalog.domain.TemplateMenu;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class SearchMenusResponse {
    private Long templateId;
    private String menuName;

    public static SearchMenusResponse from(TemplateMenu templateMenu) {
        SearchMenusResponse response = new SearchMenusResponse();

        response.templateId = templateMenu.getTemplateId();
        response.menuName = templateMenu.getMenuName();

        return response;
    }
}
