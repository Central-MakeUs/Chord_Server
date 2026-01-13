package com.coachcoach.catalog.api.response;

import com.coachcoach.catalog.domain.entity.TemplateMenu;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@ToString
@Getter
public class TemplateBasicResponse {
    private Long templateId;
    private String menuName;
    private BigDecimal defaultSellingPrice;
    private String categoryCode;
    private Integer workTime;

    public static TemplateBasicResponse from(TemplateMenu template) {
        TemplateBasicResponse response = new TemplateBasicResponse();

        response.templateId = template.getTemplateId();
        response.menuName = template.getMenuName();
        response.defaultSellingPrice = template.getDefaultSellingPrice();
        response.categoryCode = template.getMenuCategoryCode();
        response.workTime = template.getWorkTime();

        return response;
    }
}