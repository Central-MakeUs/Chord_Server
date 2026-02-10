package com.coachcoach.insight.domain.enums;

import lombok.Getter;

@Getter
public enum StrategyState {
    BEFORE("BEFORE"),
    ONGOING("ONGOING"),
    COMPLETED("COMPLETED");

    private final String value;

    StrategyState(String value) {
        this.value = value;
    }

}
