package com.coachcoach.catalog.dto.response;

import com.coachcoach.catalog.domain.MarginGrade;
import com.coachcoach.catalog.domain.Menu;

import java.math.BigDecimal;

public record MenuDetailResponse(
    Long menuId,
    String menuName,
    Integer workTime,
    BigDecimal sellingPrice,
    BigDecimal marginRate,
    BigDecimal totalCost,
    BigDecimal costRate,
    BigDecimal contributionMargin,
    String marginGradeCode,
    String marginGradeName,
    String marginGradeMessage,
    BigDecimal recommendedPrice
) {
    public static MenuDetailResponse of(Menu menu, MarginGrade marginGrade) {
        return new MenuDetailResponse(
            menu.getMenuId(),
            menu.getMenuName(),
            menu.getWorkTime(),
            menu.getSellingPrice(),
            menu.getMarginRate(),
            menu.getTotalCost(),
            menu.getCostRate(),
            menu.getContributionMargin(),
            marginGrade.getGradeCode(),
            marginGrade.getGradeName(),
            marginGrade.getMessage(),
            menu.getRecommendedPrice()
        );
    }
}