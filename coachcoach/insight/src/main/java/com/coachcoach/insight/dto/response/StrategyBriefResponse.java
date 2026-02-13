package com.coachcoach.insight.dto.response;

import com.coachcoach.insight.domain.enums.StrategyState;
import com.coachcoach.insight.domain.enums.StrategyType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record StrategyBriefResponse(
        Long menuId,
        Long strategyId,
        StrategyState state,
        StrategyType type,
        String title,
        String summary,
        String detail,
        LocalDateTime startDate,
        LocalDateTime completionDate,
        LocalDateTime createdAt
) {
}