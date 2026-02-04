package com.coachcoach.insight.domain.enums;

import lombok.Getter;

@Getter
public enum StrategyState {
    BEFORE("before"),
    ONGOING("ongoing"),
    COMPLETED("completed");

    private final String value;

    StrategyState(String value) {
        this.value = value;
    }

}
