package com.coachcoach.insight.domain;

import com.coachcoach.insight.domain.enums.StrategyState;
import com.coachcoach.insight.domain.enums.StrategyType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "tb_caution_menu_strategy")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@ToString
@Getter
public class CautionMenuStrategy implements Strategy{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long strategyId;

    private Long baselineId;

    @Column(columnDefinition = "TEXT")
    private String summary;         // 한 줄 요약

    @Column(columnDefinition = "TEXT")
    private String detail;

    @Column(columnDefinition = "TEXT")
    private String guide;

    @Column(columnDefinition = "TEXT")
    private String expectedEffect;

    @Enumerated(EnumType.STRING)
    private StrategyState state;

    @Column(name = "is_saved")
    private Boolean saved;

    private LocalDateTime startDate;

    private LocalDateTime completionDate;

    private Long menuId;    // fk

    private Long snapshotId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String guideCode;

    @Override
    public StrategyType getType() {
        return StrategyType.CAUTION;
    }

    @Override
    public String getCompletionPhrase() {
        return null;
    }

    public void updateSaved(boolean saved) {
        this.saved = saved;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateStateToOngoing() {
        this.state = StrategyState.ONGOING;
        this.startDate = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateStateToCompleted() {
        this.state = StrategyState.COMPLETED;
        this.completionDate = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
