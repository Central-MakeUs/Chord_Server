package com.coachcoach.insight.dto.response;

import com.coachcoach.insight.domain.enums.StrategyState;
import com.coachcoach.insight.domain.enums.StrategyType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record HighMarginMenuStrategyDetailResponse(
        Long strategyId,
        String summary,
        String detail,
        String guide,
        String expectedEffect,
        StrategyState state,
        LocalDateTime startDate,
        LocalDateTime completionDate,
        StrategyType type,
        Integer year,
        Integer month,
        Integer weekOfMonth,
        List<String> menuNames
) {
}
