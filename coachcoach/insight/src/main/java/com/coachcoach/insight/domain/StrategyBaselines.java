package com.coachcoach.insight.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "tb_caution_menu_strategy")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@ToString
@Getter
public class StrategyBaselines {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long baselineId;

    private Long userId;

    @Column(scale = 10, precision = 2)
    private BigDecimal avgMarginRate;

    @Column(scale = 10, precision = 2)
    private BigDecimal avgCostRate;

    @Column(scale = 10, precision = 2)
    private BigDecimal avgContributionRate;

    private LocalDate strategyDate;

    private LocalDateTime createdAt;
}
