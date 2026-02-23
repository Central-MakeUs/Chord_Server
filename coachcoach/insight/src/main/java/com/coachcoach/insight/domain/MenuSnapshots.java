package com.coachcoach.insight.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "tb_menu_snapshots")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@ToString
@Getter
public class MenuSnapshots {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long snapshotId;

    private Long baselineId;

    private Long menuId;

    private String menuCategoryCode;

    private String menuName;

    @Column(precision = 10, scale = 2)
    private BigDecimal sellingPrice;        // 판매가

    @Column(precision = 10, scale = 2)
    private BigDecimal totalCost;           // 총 원가

    @Column(precision = 10, scale = 2)
    private BigDecimal costRate;            // 원가율(%)

    @Column(precision = 10, scale = 2)
    private BigDecimal contributionMargin;  // 공헌이익

    @Column(precision = 10, scale = 2)
    private BigDecimal marginRate;          // 마진율

    private String marginGradeCode;         // 마진 등급 코드

    private Integer workTime;               // 제조 소요 시간(s)

    @Column(precision = 10, scale = 2)
    private BigDecimal recommendedPrice;    // 권장 가격

    private LocalDateTime createdAt;
}
