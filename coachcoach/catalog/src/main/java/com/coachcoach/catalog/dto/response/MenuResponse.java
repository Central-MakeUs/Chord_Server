package com.coachcoach.catalog.dto.response;

import com.coachcoach.catalog.domain.MarginGrade;
import com.coachcoach.catalog.domain.Menu;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@ToString
public class MenuResponse {
    private Long menuId;
    private String menuName;
    private BigDecimal sellingPrice;
    private BigDecimal costRate;
    private String marginGradeCode;
    private String marginGradeName;
    private BigDecimal marginRate;

    public static MenuResponse of(Menu menu, MarginGrade marginGrade){
        MenuResponse menuResponse = new MenuResponse();

        menuResponse.menuId = menu.getMenuId();
        menuResponse.menuName = menu.getMenuName();
        menuResponse.sellingPrice = menu.getSellingPrice();
        menuResponse.costRate = menu.getCostRate();
        menuResponse.marginGradeCode = marginGrade.getGradeCode();
        menuResponse.marginGradeName = marginGrade.getGradeName();
        menuResponse.marginRate = menu.getMarginRate();

        return menuResponse;
    }
}