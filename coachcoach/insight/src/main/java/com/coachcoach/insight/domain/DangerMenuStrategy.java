package com.coachcoach.insight.domain;

import com.coachcoach.insight.domain.enums.StrategyState;
import com.coachcoach.insight.domain.enums.StrategyType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "tb_danger_menu_strategy")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DangerMenuStrategy implements Strategy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long strategyId;

    private Long baselineId;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String detail;

    @Column(columnDefinition = "TEXT")
    private String guide;

    @Column(columnDefinition = "TEXT")
    private String expectedEffect;

    @Enumerated(EnumType.STRING)
    private StrategyState state;

    private LocalDateTime startDate;
    private LocalDateTime completionDate;

    private String guideCode;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "snapshot_id", insertable = false, updatable = false)
    private MenuSnapshots menuSnapshot;

    private Long menuId;

    @Override
    public Long getMenuId() {
        return menuSnapshot != null ? menuSnapshot.getMenuId() : null;
    }

    @Override
    public Long getSnapshotId() {
        return menuSnapshot.getSnapshotId();
    }

    @Override
    public StrategyType getType() {
        return StrategyType.DANGER;
    }

    @Override
    public String getCompletionPhrase() {
        return null;
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