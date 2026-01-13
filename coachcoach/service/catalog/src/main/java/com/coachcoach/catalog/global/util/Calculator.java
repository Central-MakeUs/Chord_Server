package com.coachcoach.catalog.global.util;

import com.coachcoach.catalog.entity.MarginGrade;
import com.coachcoach.catalog.entity.Unit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Calculator {
    /**
     * 재료 단가 계산 (2자리 반올림)
     * 1kg, 100g, 1개, 100ml
     * 구매가격 / 구매량 * 기준량
     */
    public BigDecimal calUnitPrice(Unit unit, BigDecimal price, BigDecimal amount) {
        return price.divide(amount, 10, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(unit.getBaseQuantity()))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 재료 단가 변동률 계산 (2자리 반올림)
     * 변동률 = ((현재가격 - 이전가격) / 이전가격) * 100
     */
    public BigDecimal calChangeRate(Unit unit, BigDecimal previousUnitPrice, BigDecimal currentUnitPrice) {
        if (previousUnitPrice == null || previousUnitPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        if (currentUnitPrice == null) {
            return BigDecimal.ZERO;
        }

        // 4자리로 나눗셈
        // 2자리 반올림

        return currentUnitPrice.subtract(previousUnitPrice)  // 4500 - 4000 = 500
                .divide(previousUnitPrice, 10, RoundingMode.HALF_UP)  // 500 / 4000 = 0.1250
                .multiply(BigDecimal.valueOf(100))  // 0.1250 * 100 = 12.50
                .setScale(2, RoundingMode.HALF_UP);  // 12.50
    }

    /**
     * 총 원가 계산
     * 제조 원가 = (재료 단가 x 중량)
     * 총 원가 = 제조 원가 + 소모품비
     * 사용자가 입력한 가격 모두 합산
     */
    public BigDecimal calTotalCost(
            List<BigDecimal> costs
    ) {
        return costs.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 원가율 계산 (소수점 2자리까지 허용)
     * (한 잔당 원가 / 한 잔 판매가) x 100
     */
    public BigDecimal calCostRate(
            BigDecimal totalCost,
            BigDecimal sellingPrice
    ) {
        // 판매가 0으로 설정되어 있는 경우
        if(sellingPrice.compareTo(BigDecimal.ZERO) == 0){
            return BigDecimal.ZERO;
        }
        return totalCost
                .divide(sellingPrice, 10, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 공헌이익률 계산
     * (판매가 - (제조원가 + 1잔 당 인건비)) / 판매가 x 100
     */
    public BigDecimal calContributionMargin(
            BigDecimal sellingPrice,
            BigDecimal totalCost,
            BigDecimal laborCostPerCup
    ) {
        if(sellingPrice.compareTo(BigDecimal.ZERO) == 0){
            return BigDecimal.ZERO;
        }

        BigDecimal contribution = sellingPrice
                .subtract(totalCost)
                .subtract(laborCostPerCup);

        return contribution
                .divide(sellingPrice, 10, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 1잔 당 인건비 계산
     * 제조시간(초) / 60 x (시간 당 인건비 / 360)
     */
    public BigDecimal calLaborCostPerCup(
            Integer workTime,
            BigDecimal laborCostPerHour
    ) {
        return BigDecimal.valueOf(workTime)
                .divide(BigDecimal.valueOf(60), 10, RoundingMode.HALF_UP)
                .multiply(laborCostPerHour)
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 마진율 계산
     * (판매가 - (총 원가 + 1잔당 인건비)) / 판매가 x 100
     */
    public BigDecimal calMarginRate(
            BigDecimal sellingPrice,
            BigDecimal totalCost,
            BigDecimal laborCostPerCup
    ) {
        if(sellingPrice.compareTo(BigDecimal.ZERO) == 0){
            return BigDecimal.ZERO;
        }

        BigDecimal contribution = sellingPrice
                .subtract(totalCost)
                .subtract(laborCostPerCup);

        return contribution
                .divide(sellingPrice, 10, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 마진 등급 계산
     * 마진 코드 반환
     */
    public String calMarginGrade(BigDecimal marginRate) {
        if(marginRate.compareTo(BigDecimal.valueOf(25)) <= 0) {
            // 25% 이하
            return "SAFE";
        } else if(marginRate.compareTo(BigDecimal.valueOf(35)) <= 0) {
            // 35% 이하
            return "NORMAL";
        } else if(marginRate.compareTo(BigDecimal.valueOf(40)) <= 0) {
            // 40% 이하
            return "CAUTION";
        } else {
            // 40% 초과
            return "DANGER";
        }
    }

    /**
     * 권장 가격 계산
     * 총 원가 / 0.3
     */
    public BigDecimal calRecommendedPrice(BigDecimal totalCost) {
        return totalCost
                .divide(BigDecimal.valueOf(0.3), 0, RoundingMode.HALF_UP);
    }
}
