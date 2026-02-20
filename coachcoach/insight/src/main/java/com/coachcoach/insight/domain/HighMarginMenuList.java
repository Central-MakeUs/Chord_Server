package com.coachcoach.insight.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "tb_high_margin_menu_list")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@ToString
@Getter
public class HighMarginMenuList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long strategyId;

    private Long menuId;

    private Long snapshotId;

    private LocalDateTime createdAt;
}
