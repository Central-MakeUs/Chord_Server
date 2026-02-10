package com.coachcoach.insight.dto.response;

import com.coachcoach.insight.domain.enums.StrategyState;
import com.coachcoach.insight.domain.enums.StrategyType;

import java.time.LocalDateTime;

public record SavedStrategyResponse(
        Long strategyId,
        StrategyState state,
        StrategyType type,
        String summary,
        String detail,
        Integer year,
        Integer month,
        Integer weekOfMonth,
        LocalDateTime createdAt
) {
}
