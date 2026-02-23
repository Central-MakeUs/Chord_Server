package com.coachcoach.insight.domain.enums;

import com.coachcoach.common.exception.BusinessException;
import com.coachcoach.insight.exception.InsightErrorCode;
import lombok.Getter;

@Getter
public enum StrategyType {
    DANGER,
    HIGH_MARGIN,
    CAUTION
    ;

    public static StrategyType from(String value) {
        if (value == null) return null;

        return switch (value.toUpperCase()) {
            case "DANGER" -> DANGER;
            case "HIGH_MARGIN" -> HIGH_MARGIN;
            case "CAUTION" -> CAUTION;
            default -> throw new BusinessException(InsightErrorCode.NOTFOUND_STRATEGY_TYPE);
        };
    }
}