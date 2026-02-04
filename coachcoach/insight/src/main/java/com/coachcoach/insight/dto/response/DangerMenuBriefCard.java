package com.coachcoach.insight.dto.response;


import com.coachcoach.insight.domain.DangerMenuStrategy;
import com.coachcoach.insight.domain.enums.StrategyState;

public record DangerMenuBriefCard(
        Long strategyId,
        String summary,
        String detail,
        StrategyState state
) {
    public static DangerMenuBriefCard from(DangerMenuStrategy strategy) {
        return new DangerMenuBriefCard(
                strategy.getStrategyId(),
                strategy.getSummary(),
                strategy.getDetail(),
                strategy.getState()
        );
    }
}
