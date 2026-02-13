package com.coachcoach.insight.dto.response;

import com.coachcoach.insight.domain.enums.StrategyState;
import com.coachcoach.insight.domain.enums.StrategyType;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record SavedStrategyResponse(
        Long strategyId,
        StrategyState state,
        StrategyType type,
        String summary,
        String detail,
        Integer year,
        Integer month,
        Integer weekOfMonth,
        Long menuId,
        String title,       // menuName or title
        LocalDateTime createdAt,
        LocalDate strategyDate
) {
}
