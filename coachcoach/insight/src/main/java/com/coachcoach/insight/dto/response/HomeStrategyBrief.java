package com.coachcoach.insight.dto.response;

import com.coachcoach.insight.domain.enums.StrategyState;
import com.coachcoach.insight.domain.enums.StrategyType;

import java.time.LocalDateTime;

public record HomeStrategyBrief(
        Long menuId,
        String menuName,
        Long strategyId,
        StrategyState state,
        StrategyType type,
        String summary,
        LocalDateTime createdAt
) {
}
