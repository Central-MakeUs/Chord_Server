package com.coachcoach.insight.dto.response;

import com.coachcoach.insight.domain.enums.StrategyState;
import com.coachcoach.insight.domain.enums.StrategyType;

import java.time.LocalDateTime;

public record StrategyBriefResponse(
        Long strategyId,
        StrategyState state,
        StrategyType type,
        String summary,
        String detail,
        LocalDateTime startDate,
        LocalDateTime completionDate,
        LocalDateTime createdAt
) {
}