package com.coachcoach.insight.dto.response;

import com.coachcoach.insight.domain.enums.StrategyState;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record NeedManagementMenu(
        Long strategyId,
        Long menuId,
        String menuName,
        BigDecimal costRate,
        BigDecimal marginRate,
        String marginGradeCode,
        StrategyState state
) {
}
