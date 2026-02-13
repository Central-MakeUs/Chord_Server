package com.coachcoach.insight.dto.response;

import com.coachcoach.insight.domain.enums.StrategyState;
import com.coachcoach.insight.domain.enums.StrategyType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record DangerMenuStrategyDetailResponse(
        Long strategyId,
        String summary,
        String detail,
        String guide,
        String expectedEffect,
        StrategyState state,
        Boolean saved,
        LocalDateTime startDate,
        LocalDateTime completionDate,
        Long menuId,
        String menuName,
        BigDecimal costRate,
        StrategyType type,
        Integer year,
        Integer month,
        Integer weekOfMonth
) {
}
