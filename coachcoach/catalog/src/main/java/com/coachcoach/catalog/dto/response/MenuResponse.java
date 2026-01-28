package com.coachcoach.catalog.dto.response;

import com.coachcoach.catalog.domain.MarginGrade;
import com.coachcoach.catalog.domain.Menu;

import java.math.BigDecimal;

public record MenuResponse (
    Long menuId,
    String menuName,
    BigDecimal sellingPrice,
    BigDecimal costRate,
    String marginGradeCode,
    String marginGradeName,
    BigDecimal marginRate
) {
    public static MenuResponse of(Menu menu, MarginGrade marginGrade){
        return new MenuResponse(
            menu.getMenuId(),
            menu.getMenuName(),
            menu.getSellingPrice(),
            menu.getCostRate(),
            marginGrade.getGradeCode(),
            marginGrade.getGradeName(),
            menu.getMarginRate()
        );
    }
}