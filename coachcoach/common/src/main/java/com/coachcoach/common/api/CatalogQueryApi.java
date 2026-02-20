package com.coachcoach.common.api;

import com.coachcoach.common.dto.internal.MenuInfo;
import com.coachcoach.common.dto.internal.StoreInfo;
import com.coachcoach.common.dto.internal.UserInfo;

import java.math.BigDecimal;
import java.util.List;

public interface CatalogQueryApi {
    int countByUserIdAndMarginGradeCode(Long userId, String marginGradeCode);

    void deleteByUserId(Long userId);

    List<MenuInfo> findByMenuIdIn(List<Long> menuIds);
    MenuInfo findByUserIdAndMenuId(Long userId, Long menuId);
    BigDecimal getAvgMarginRate(Long userId);
    void updateMenusByUpdateLaborCost(Long userId, BigDecimal laborCost, Boolean includeWeeklyHolidayPay);
}
