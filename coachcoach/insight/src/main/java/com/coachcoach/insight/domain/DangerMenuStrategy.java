package com.coachcoach.insight.domain;

import com.coachcoach.insight.domain.enums.StrategyState;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "tb_danger_menu_strategy")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@ToString
@Getter
public class DangerMenuStrategy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long strategyId;

    @Column(columnDefinition = "TEXT")
    private String summary;         // 한 줄 요약

    @Column(columnDefinition = "TEXT")
    private String detail;

    @Column(columnDefinition = "TEXT")
    private String guide;

    @Column(columnDefinition = "TEXT")
    private String completionPhrase;

    private Long menuId;    // fk

    private Long userId;

    private LocalDate strategyDate;

    private LocalDate startDate;

    private LocalDate completionDate;

    @Enumerated(EnumType.STRING)
    private StrategyState state;

    @Column(name = "is_saved")
    private Boolean saved;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public void updateSaved(boolean saved) {
        this.saved = saved;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateStateToOngoing() {
        this.state = StrategyState.ONGOING;
        this.startDate = LocalDate.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateStateToCompleted() {
        this.state = StrategyState.COMPLETED;
        this.completionDate = LocalDate.now();
        this.updatedAt = LocalDateTime.now();
    }
}
