package com.coachcoach.catalog.dto.response;

import com.coachcoach.catalog.domain.TemplateMenu;

import java.math.BigDecimal;

public record TemplateBasicResponse (
    Long templateId,
    String menuName,
    BigDecimal defaultSellingPrice,
    String categoryCode,
    Integer workTime
) {
    public static TemplateBasicResponse from(TemplateMenu template) {
        return new TemplateBasicResponse(
            template.getTemplateId(),
            template.getMenuName(),
            template.getDefaultSellingPrice(),
            template.getMenuCategoryCode(),
            template.getWorkTime()
        );
    }
}