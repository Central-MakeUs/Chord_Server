package com.coachcoach.insight.domain;

import com.coachcoach.insight.domain.enums.StrategyState;
import com.coachcoach.insight.domain.enums.StrategyType;

import java.time.LocalDateTime;

public interface Strategy {
    Long getStrategyId();
    Long getBaselineId();
    Long getMenuId();
    Long getSnapshotId();
    String getSummary();
    String getDetail();
    String getGuide();
    String getExpectedEffect();
    StrategyState getState();
    LocalDateTime getStartDate();
    LocalDateTime getCompletionDate();
    String getGuideCode();
    StrategyType getType();
    String getCompletionPhrase();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();

    void updateStateToOngoing();
    void updateStateToCompleted();
}