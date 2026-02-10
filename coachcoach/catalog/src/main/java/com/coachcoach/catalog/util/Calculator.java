package com.coachcoach.catalog.util;

import com.coachcoach.catalog.domain.Menu;
import com.coachcoach.catalog.dto.response.MenuCostAnalysis;
import com.coachcoach.catalog.domain.Ingredient;
import com.coachcoach.catalog.domain.Recipe;
import com.coachcoach.catalog.domain.Unit;
import com.coachcoach.catalog.repository.IngredientRepository;
import com.coachcoach.catalog.exception.CatalogErrorCode;
import com.coachcoach.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class Calculator {
    private final IngredientRepository ingredientRepository;
    private final CodeFinder codeFinder;

    /**
     * laborcost (시급) 계산
     */
    public BigDecimal calLaborCost(Boolean includeWeeklyHolidayPay, BigDecimal laborCost){
        if(includeWeeklyHolidayPay == Boolean.FALSE) {
            return laborCost;
        }

        return laborCost.multiply(BigDecimal.valueOf(1.2))
                .setScale(1, RoundingMode.HALF_UP);
    }

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
            return null;
        }

        if (currentUnitPrice == null) {
            return BigDecimal.ZERO;
        }

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
     * 특정 메뉴의 총 원가 계산
     * 입력 필드: userId, List<Recipe>
     */
    public BigDecimal calTotalCostWithRecipes(
            Long userId,
            List<Recipe> recipes
    ) {
        List<Long> ingredientIds = recipes.stream()
                .map(Recipe::getIngredientId)
                .toList();
        Map<Long, Ingredient> ingredientMap = ingredientRepository.findByUserIdAndIngredientIdIn(userId, ingredientIds).stream()
                .collect(Collectors.toMap(Ingredient::getIngredientId, Function.identity()));

        return recipes.stream()
                .map(x -> {
                    Ingredient i = ingredientMap.get(x.getIngredientId());

                    if(i == null) {
                        throw new BusinessException(CatalogErrorCode.NOTFOUND_INGREDIENT);
                    }

                    Unit unit = codeFinder.findUnitByCode(i.getUnitCode());

                    return i.getCurrentUnitPrice()
                            .divide(BigDecimal.valueOf(unit.getBaseQuantity()), 10, RoundingMode.HALF_UP)
                            .multiply(x.getAmount());
                })
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
     * (시간 당 인건비 / 3600) * 한 잔 제조 시간
     */
    public BigDecimal calLaborCostPerCup(
            Integer workTime,
            BigDecimal laborCostPerHour
    ) {
        return laborCostPerHour
                .divide(BigDecimal.valueOf(3600), 10, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(workTime))
                .setScale(1, RoundingMode.HALF_UP);
    }

    /**
     * 마진률 계산
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
    public String calMarginGrade(BigDecimal costRate) {
        if(costRate.compareTo(BigDecimal.valueOf(25)) <= 0) {
            // 25% 이하
            return "SAFE";
        } else if(costRate.compareTo(BigDecimal.valueOf(35)) <= 0) {
            // 35% 이하
            return "NORMAL";
        } else if(costRate.compareTo(BigDecimal.valueOf(40)) <= 0) {
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

    public MenuCostAnalysis calAnalysis(
            BigDecimal totalCost,
            BigDecimal sellingPrice,
            BigDecimal laborCost,
            Integer workTime
    ) {
        // 원가율 계산
        BigDecimal costRate = calCostRate(totalCost, sellingPrice);

        // 공헌이익률 계산
        BigDecimal laborCostPerCup = calLaborCostPerCup(workTime, laborCost);
        BigDecimal contributionMargin = calContributionMargin(sellingPrice, totalCost, laborCostPerCup);

        // 마진율 계산
        BigDecimal marginRate = calMarginRate(sellingPrice, totalCost, laborCostPerCup);

        // 마진 등급 코드 계산
        String marginCode = calMarginGrade(costRate);

        // 권장 가격 계산
        BigDecimal recommendedPrice = calRecommendedPrice(totalCost);

        return new MenuCostAnalysis(costRate, contributionMargin, marginRate, marginCode, recommendedPrice);
    }

    /**
     * 가게 평균 원가율 계산
     * 소수점 2자리까지 표시
     */
    public BigDecimal calAvgCostRate(
            List<Menu> menus
    ) {
        if (menus == null || menus.isEmpty()) {
            return BigDecimal.ZERO;
        }

        int numOfMenus = menus.size();  // 메뉴 개수
        BigDecimal totalCostRate = menus.stream()
                .reduce(BigDecimal.ZERO,
                        (subtotal, element) -> subtotal.add(element.getCostRate()),
                        BigDecimal::add
                );      // 원가율 총합
        return totalCostRate.divide(BigDecimal.valueOf(numOfMenus), 10, RoundingMode.HALF_UP)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 가게 평균 마진율 계산
     * 소수점 2자리까지 표시
     */
    public BigDecimal calAvgMarginRate(
            List<Menu> menus
    ) {
        if (menus == null || menus.isEmpty()) {
            return BigDecimal.ZERO;
        }

        int numOfMenus = menus.size();  // 메뉴 개수
        BigDecimal totalContributionMargin = menus.stream()
                .reduce(BigDecimal.ZERO, (subtotal, element) -> subtotal.add(element.getMarginRate()),
                        BigDecimal::add
                );      // 마진율 총합
        return totalContributionMargin.divide(BigDecimal.valueOf(numOfMenus), 10, RoundingMode.HALF_UP)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
