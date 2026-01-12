package com.coachcoach.catalog.service;

import com.coachcoach.catalog.entity.*;
import com.coachcoach.catalog.global.exception.CatalogErrorCode;
import com.coachcoach.catalog.global.util.Cache;
import com.coachcoach.catalog.global.util.CodeFinder;
import com.coachcoach.catalog.repository.*;
import com.coachcoach.catalog.service.request.IngredientCreateRequest;
import com.coachcoach.catalog.service.request.IngredientUpdateRequest;
import com.coachcoach.catalog.service.request.SupplierUpdateRequest;
import com.coachcoach.catalog.service.response.*;
import com.coachcoach.common.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CatalogService {

    private final Cache cache;
    private final CodeFinder codeFinder;
    private final IngredientCategoryRepository ingredientCategoryRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private final IngredientRepository ingredientRepository;
    private final MenuRepository menuRepository;
    private final IngredientPriceHistoryRepository ingredientPriceHistoryRepository;

    /**
     * 재료 카테고리 목록 조회
     */
    public List<IngredientCategoryResponse> readIngredientCategory() {
        // 정렬 조건: display order asc
        return cache.getIngredientCategories().stream()
                .map(IngredientCategoryResponse::from)
                .toList();
    }

    /**
     * 카테고리 별 재료 목록 반환 (필터링, 복수 선택 가능)
     * 정렬 조건: ingredientId Desc (최신순)
     */
    public List<IngredientResponse> readIngredientsByCategory(Long userId, List<String> category) {
        if(category == null) {
            // 전체 조회
            return ingredientRepository.findAllByUserIdOrderByIngredientIdDesc(userId)
                    .stream()
                    .map(x -> IngredientResponse.of(x, codeFinder.findUnitByCode(x.getUnitCode()).getBaseQuantity()))
                    .toList();
        }

        // 카테고리 유효성 검사
        if (category.stream().anyMatch(code -> !codeFinder.existsIngredientCategory(code))) {
            throw new BusinessException(CatalogErrorCode.NOTFOUND_CATEGORY);
        }

        // 부분 조회
        return ingredientRepository.findByUserIdAndIngredientCategoryCodeInOrderByIngredientIdDesc(userId, category)
                .stream()
                .map(x -> IngredientResponse.of(x, codeFinder.findUnitByCode(x.getUnitCode()).getBaseQuantity()))
                .toList();

    }


    /**
     * 재료 생성(재료명, 가격, 사용량, 단위, 카테고리)
     */
    @Transactional
    public IngredientResponse createIngredient(Long userId, IngredientCreateRequest request) {
        // 재료 카테고리 & 유닛 유효성 검증
        if(!codeFinder.existsIngredientCategory(request.getCategoryCode())) {
            throw new BusinessException(CatalogErrorCode.NOTFOUND_CATEGORY);
        } else if(!codeFinder.existsUnit(request.getUnitCode())) {
            throw new BusinessException(CatalogErrorCode.NOTFOUND_UNIT);
        }

        // 중복 확인 (userId + ingredientName)
        if(ingredientRepository.existsByUserIdAndIngredientName(userId, request.getIngredientName())) {
            throw new BusinessException(CatalogErrorCode.DUP_INGREDIENT);
        }

        // 단가 계산
        Unit unit = codeFinder.findUnitByCode(request.getUnitCode());
        BigDecimal unitPrice = calUnitPrice(unit, request.getPrice(), request.getAmount());

        // 재료 단가 입력
        Ingredient ingredient = ingredientRepository.save(
                Ingredient.create(
                userId,
                request.getCategoryCode(),
                request.getIngredientName(),
                request.getUnitCode(),
                unitPrice,
                request.getSupplier()
        ));

        // 히스토리 입력
        IngredientPriceHistory ingredientPriceHistory = ingredientPriceHistoryRepository.save(
                IngredientPriceHistory.create(
                ingredient.getIngredientId(),
                unitPrice,
                request.getAmount(),
                request.getPrice(),
                null
        ));

        return IngredientResponse.of(ingredient, unit.getBaseQuantity());
    }

    /**
     * 재료 상세
     */
    public IngredientDetailResponse readIngredientDetail(Long userId, Long ingredientId) {
        // 단가 조회
        Ingredient ingredient = ingredientRepository.findByUserIdAndIngredientId(userId, ingredientId)
                .orElseThrow(() -> new BusinessException(CatalogErrorCode.NOTFOUND_INGREDIENT));
        Unit unit = codeFinder.findUnitByCode(ingredient.getUnitCode());

        //사용 중인 메뉴 조회
        List<String> menus = menuRepository.findMenusByUserIdAndIngredientId(userId, ingredientId);

        // 히스토리 조회 (가장 최신)
        IngredientPriceHistory iph = ingredientPriceHistoryRepository.findFirstByIngredientIdOrderByHistoryIdDesc(ingredientId)
                .orElseThrow(() -> new BusinessException(CatalogErrorCode.NOTFOUND_PRICEHISTORY));

        return IngredientDetailResponse.of(
                ingredient,
                unit.getBaseQuantity(),
                menus,
                iph
        );
    }

    /**
     * 가격 변경 이력 목록
     */
    public List<PriceHistoryResponse> readIngredientPriceHistory(Long userId, Long ingredientId) {
        // 변경 이력 조회
        List<IngredientPriceHistory> results = ingredientPriceHistoryRepository.findByIngredientIdOrderByHistoryIdDesc(ingredientId);

        // 단위 조회
        Ingredient ingredient = ingredientRepository.findByUserIdAndIngredientId(userId, ingredientId).orElseThrow(() -> new BusinessException(CatalogErrorCode.NOTFOUND_INGREDIENT));
        String unitCode = ingredient.getUnitCode();
        Integer baseQuantity = codeFinder.findUnitByCode(ingredient.getUnitCode()).getBaseQuantity();

        return results.stream()
                .map(x -> PriceHistoryResponse.of(
                        x,
                        unitCode,
                        baseQuantity
                ))
                .toList();
    }

    /**
     * 즐겨찾기 설정/해제
     */
    @Transactional
    public void updateFavorite(Long userId, Long ingredientId, Boolean favorite) {
        Ingredient ingredient = ingredientRepository.findByUserIdAndIngredientId(userId, ingredientId).orElseThrow(() -> new BusinessException(CatalogErrorCode.NOTFOUND_INGREDIENT));
        ingredient.updateFavorite(favorite);
    }

    /**
     * 재료 단가 수정
     */
    @Transactional
    public IngredientUpdateResponse updateIngredient(Long userId, Long ingredientId, IngredientUpdateRequest request) {
        // 재료 조회
        Ingredient ingredient = ingredientRepository.findByUserIdAndIngredientId(userId, ingredientId).orElseThrow(() -> new BusinessException(CatalogErrorCode.NOTFOUND_INGREDIENT));
        Unit previousUnit = codeFinder.findUnitByCode(ingredient.getUnitCode());
        Unit currentUnit = codeFinder.findUnitByCode(request.getUnitCode());

        //단가 계산
        BigDecimal unitPrice = calUnitPrice(currentUnit,  request.getPrice(), request.getAmount());

        // 가격 변동률 계산 (단위가 바뀐 경우 null로 설정)
        BigDecimal changeRate = (currentUnit.equals(previousUnit)) ? calChangeRate(currentUnit, ingredient.getCurrentUnitPrice(), unitPrice) : null;

        // 가격 업데이트
        ingredient.update(unitPrice, currentUnit.getUnitCode());

        // 히스토리 업데이트
        IngredientPriceHistory iph = ingredientPriceHistoryRepository.save(
                IngredientPriceHistory.create(
                        ingredientId,
                        unitPrice,
                        request.getAmount(),
                        request.getPrice(),
                        changeRate
                )
        );

        // 변환
        return IngredientUpdateResponse.of(
                unitPrice,
                currentUnit.getBaseQuantity(),
                currentUnit.getUnitCode(),
                iph
        );
    }

    /**
     * 재료 공급업체 수정
     */
    @Transactional
    public SupplierUpdateResponse updateIngredientSupplier(Long userId, Long ingredientId, SupplierUpdateRequest request) {
        Ingredient ingredient = ingredientRepository.findByUserIdAndIngredientId(userId, ingredientId).orElseThrow(() -> new BusinessException(CatalogErrorCode.NOTFOUND_INGREDIENT));
        ingredient.updateSupplier(request.getSupplier());

        return SupplierUpdateResponse.from(ingredient);
    }

    /**
     * 메뉴 카테고리 목록 조회
     */
    public List<MenuCategoryResponse> readMenuCategory() {
        // 정렬 조건: display order asc
        return cache.getMenuCategories().stream()
                .map(MenuCategoryResponse::from)
                .toList();
    }

    /**
     * 재료 단가 계산 (2자리 반올림)
     * 1kg, 100g, 1개, 100ml
     * 구매가격 / 구매량 * 기준량
     */
    private BigDecimal calUnitPrice(Unit unit, BigDecimal price, BigDecimal amount) {
        return price.divide(amount, 2, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(unit.getBaseQuantity()));
    }

    /**
     * 재료 단가 변동률 계산 (2자리 반올림)
     * 변동률 = ((현재가격 - 이전가격) / 이전가격) * 100
     */
    private BigDecimal calChangeRate(Unit unit, BigDecimal previousUnitPrice, BigDecimal currentUnitPrice) {
        if (previousUnitPrice == null || previousUnitPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        if (currentUnitPrice == null) {
            return BigDecimal.ZERO;
        }

        //
        BigDecimal difference = currentUnitPrice.subtract(previousUnitPrice);
        // 4자리로 나눗셈
        // 2자리 반올림

        return currentUnitPrice.subtract(previousUnitPrice)  // 4500 - 4000 = 500
                .divide(previousUnitPrice, 4, RoundingMode.HALF_UP)  // 500 / 4000 = 0.1250
                .multiply(BigDecimal.valueOf(100))  // 0.1250 * 100 = 12.50
                .setScale(2, RoundingMode.HALF_UP);  // 12.50
    }
}
