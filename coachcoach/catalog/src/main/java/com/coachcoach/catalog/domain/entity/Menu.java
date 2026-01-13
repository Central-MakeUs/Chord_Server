package com.coachcoach.catalog.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "tb_menu")
@Getter
@ToString
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long menuId;
    private Long userId;
    private String menuCategoryCode;
    private String menuName;
    @Column(precision = 10, scale = 2)
    private BigDecimal sellingPrice;        // 판매가
    @Column(precision = 10, scale = 2)
    private BigDecimal totalCost;           // 총 원가
    @Column(precision = 5, scale = 2)
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
    private LocalDateTime updatedAt;

    public static Menu create(
            Long userId,
            String menuCategoryCode,
            String menuName,
            BigDecimal sellingPrice,
            BigDecimal totalCost,
            BigDecimal costRate,
            BigDecimal contributionMargin,
            BigDecimal marginRate,
            String marginGradeCode,
            Integer workTime,
            BigDecimal recommendedPrice
    ) {
        Menu menu = new Menu();

        menu.userId = userId;
        menu.menuCategoryCode = menuCategoryCode;
        menu.menuName = menuName;
        menu.sellingPrice = sellingPrice;
        menu.totalCost = totalCost;
        menu.costRate = costRate;
        menu.contributionMargin = contributionMargin;
        menu.marginRate = marginRate;
        menu.marginGradeCode = marginGradeCode;
        menu.workTime = workTime;
        menu.recommendedPrice = recommendedPrice;
        menu.createdAt = LocalDateTime.now();
        menu.updatedAt = LocalDateTime.now();

        return menu;
    }

    public void update(
            BigDecimal totalCost,
            BigDecimal costRate,
            BigDecimal contributionMargin,
            BigDecimal marginRate,
            String marginGradeCode,
            BigDecimal recommendedPrice
    ) {
        this.totalCost = totalCost;
        this.costRate = costRate;
        this.contributionMargin = contributionMargin;
        this.marginRate = marginRate;
        this.marginGradeCode = marginGradeCode;
        this.recommendedPrice = recommendedPrice;
        this.updatedAt = LocalDateTime.now();
    }
}
