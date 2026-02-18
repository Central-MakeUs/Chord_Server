package com.coachcoach.insight.domain;

import com.coachcoach.insight.domain.enums.StrategyState;
import com.coachcoach.insight.domain.enums.StrategyType;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface Strategy {
    Long getStrategyId();
    Long getBaselineId();
    Long getMenuId();
    String getSummary();
    String getDetail();
    String getGuide();
    String getExpectedEffect();
    StrategyState getState();
    Boolean getSaved();
    LocalDateTime getStartDate();
    LocalDateTime getCompletionDate();
    String getGuideCode();
    StrategyType getType();
    String getCompletionPhrase();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();

    void updateSaved(boolean saved);
    void updateStateToOngoing();
    void updateStateToCompleted();
}