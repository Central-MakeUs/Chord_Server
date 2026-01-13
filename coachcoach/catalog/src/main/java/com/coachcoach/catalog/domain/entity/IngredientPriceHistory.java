package com.coachcoach.catalog.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "tb_ingredient_price_history")
@Entity
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IngredientPriceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;
    private Long ingredientId;              // 재료 고유 ID
    @Column(precision = 10, scale = 2)
    private BigDecimal unitPrice;           // 구매 단가
    private String unitCode;
    @Column(precision = 10, scale = 3)
    private BigDecimal originalAmount;      // 사용자 구매량
    @Column(precision = 10, scale = 2)
    private BigDecimal originalPrice;       // 구매 가격
    @Column(precision = 10, scale = 2)
    private BigDecimal changeRate;          // 변동률
    private LocalDateTime createdAt;

    public static IngredientPriceHistory create(
            Long ingredientId,
            BigDecimal unitPrice, String unitCode,
            BigDecimal originalAmount, BigDecimal originalPrice, BigDecimal changeRate
    ) {
        IngredientPriceHistory iph = new IngredientPriceHistory();

        iph.ingredientId = ingredientId;
        iph.unitPrice = unitPrice;
        iph.unitCode = unitCode;
        iph.originalAmount = originalAmount;
        iph.originalPrice = originalPrice;
        iph.changeRate = changeRate;
        iph.createdAt = LocalDateTime.now();

        return iph;
    }
}
