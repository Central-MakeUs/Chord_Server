package com.coachcoach.insight.domain;

import com.coachcoach.insight.domain.enums.StrategyState;
import com.coachcoach.insight.domain.enums.StrategyStateConverter;
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
public class CautionMenuStrategy {
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

    private Long userId;    // fk

    private LocalDate strategyDate;

    private LocalDate startDate;

    private LocalDate completionDate;

    @Convert(converter = StrategyStateConverter.class)
    private StrategyState state;

    @Column(name = "is_saved")
    private Boolean saved;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
