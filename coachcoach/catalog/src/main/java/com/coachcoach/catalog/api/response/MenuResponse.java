package com.coachcoach.catalog.api.response;

import com.coachcoach.catalog.domain.entity.Menu;
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

    public static MenuResponse of(Menu menu, String gradeName){
        MenuResponse menuResponse = new MenuResponse();

        menuResponse.menuId = menu.getMenuId();
        menuResponse.menuName = menu.getMenuName();
        menuResponse.sellingPrice = menu.getSellingPrice();
        menuResponse.costRate = menu.getCostRate();
        menuResponse.marginGradeCode = menu.getMarginGradeCode();
        menuResponse.marginGradeName = gradeName;
        menuResponse.marginRate = menu.getMarginRate();

        return menuResponse;
    }
}