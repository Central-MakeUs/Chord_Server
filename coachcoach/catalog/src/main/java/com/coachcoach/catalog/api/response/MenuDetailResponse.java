package com.coachcoach.catalog.api.response;

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
    private String marginCode;
    private String marginName;
    private String marginMessage;
    private BigDecimal recommendedPrice;

    public static MenuDetailResponse of(Menu menu, String marginName, String marginMessage) {
        MenuDetailResponse response = new  MenuDetailResponse();

        response.menuId = menu.getMenuId();
        response.menuName = menu.getMenuName();
        response.workTime = menu.getWorkTime();
        response.sellingPrice = menu.getSellingPrice();
        response.marginRate = menu.getMarginRate();
        response.totalCost = menu.getTotalCost();
        response.costRate = menu.getCostRate();
        response.contributionMargin = menu.getContributionMargin();
        response.marginCode = menu.getMarginGradeCode();
        response.marginName = marginName;
        response.marginMessage = marginMessage;
        response.recommendedPrice = menu.getRecommendedPrice();

        return response;
    }
}