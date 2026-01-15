package com.coachcoach.catalog.api.response;

import com.coachcoach.catalog.domain.entity.MarginGrade;
import com.coachcoach.catalog.domain.entity.Menu;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@ToString
public class MenuDetailResponse {
    private Long menuId;
    private String menuName;
    private Integer workTime;
    private BigDecimal sellingPrice;
    private BigDecimal marginRate;
    private BigDecimal totalCost;
    private BigDecimal costRate;
    private BigDecimal contributionMargin;
    private String marginGradeCode;
    private String marginGradeName;
    private String marginGradeMessage;
    private BigDecimal recommendedPrice;

    public static MenuDetailResponse of(Menu menu, MarginGrade marginGrade) {
        MenuDetailResponse response = new  MenuDetailResponse();

        response.menuId = menu.getMenuId();
        response.menuName = menu.getMenuName();
        response.workTime = menu.getWorkTime();
        response.sellingPrice = menu.getSellingPrice();
        response.marginRate = menu.getMarginRate();
        response.totalCost = menu.getTotalCost();
        response.costRate = menu.getCostRate();
        response.contributionMargin = menu.getContributionMargin();
        response.marginGradeCode = marginGrade.getGradeCode();
        response.marginGradeName = marginGrade.getGradeName();
        response.marginGradeMessage = marginGrade.getMessage();
        response.recommendedPrice = menu.getRecommendedPrice();

        return response;
    }
}