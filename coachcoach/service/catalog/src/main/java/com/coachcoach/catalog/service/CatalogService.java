package com.coachcoach.catalog.service;

import com.coachcoach.catalog.entity.Ingredient;
import com.coachcoach.catalog.entity.IngredientCategory;
import com.coachcoach.catalog.entity.IngredientPriceHistory;
import com.coachcoach.catalog.entity.MenuCategory;
import com.coachcoach.catalog.global.exception.CatalogErrorCode;
import com.coachcoach.catalog.repository.IngredientCategoryRepository;
import com.coachcoach.catalog.repository.IngredientPriceHistoryRepository;
import com.coachcoach.catalog.repository.IngredientRepository;
import com.coachcoach.catalog.repository.MenuCategoryRepository;
import com.coachcoach.catalog.service.response.IngredientCategoryResponse;
import com.coachcoach.catalog.service.response.IngredientResponse;
import com.coachcoach.catalog.service.response.MenuCategoryResponse;
import com.coachcoach.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogService {

    private final CacheService cacheService;
    private final IngredientCategoryRepository ingredientCategoryRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private final IngredientRepository ingredientRepository;
    private final IngredientPriceHistoryRepository ingredientPriceHistoryRepository;

    /**
     * 재료 카테고리 목록 조회
     */
    public List<IngredientCategoryResponse> readIngredientCategory() {
        // 정렬 조건: display order asc
        return cacheService.getIngredientCategories().stream()
                .map(IngredientCategoryResponse::from)
                .toList();
    }

    /**
     * 카테고리 별 재료 목록 반환 (필터링, 복수 선택 가능)
     */
//    public List<IngredientResponse> readIngredientsByCategory(Long userId, List<String> category) {
//        // 재료
//    }


    /**
     * 재료 생성(재료명, 가격, 사용량, 단위, 카테고리)
     */
//    @Transactional
//    public void createIngredient(Long userId, IngredientCreateRequest request) {
//        // 중복 확인 (userId + ingredientName)
//        if(ingredientRepository.existsByUserIdAndIngredientName(userId, request.getIngredientName())) {
//            throw new BusinessException(CatalogErrorCode.DUP_INGREDIENT);
//        }
//
//        // 단가 계산
//        BigDecimal unitPrice = calUnitPrice(request.getUnit(), request.getOriginalPrice(), request.getOriginalAmount());
//
//        // 재료 단가 입력
//        Ingredient ingredient = ingredientRepository.save(
//                Ingredient.create(
//                        userId,
//                        request.getIngredientCategoryId(),
//                        request.getIngredientName(),
//                        request.getUnit(),
//                        unitPrice,
//                        (request.getSupplier() == null || request.getSupplier().isBlank()) ? null : request.getSupplier()
//                )
//        );
//
//        // 히스토리 입력
//        IngredientPriceHistory iph = ingredientPriceHistoryRepository.save(
//                IngredientPriceHistory.create(
//                        ingredient.getIngredientId(),
//                        unitPrice,
//                        request.getOriginalAmount(), request.getOriginalPrice(), new BigDecimal(0)
//                )
//        );
//    }

    /**
     * 메뉴 카테고리 목록 조회
     */
    public List<MenuCategoryResponse> readMenuCategory() {
        // 정렬 조건: display order asc
        return cacheService.getMenuCategories().stream()
                .map(MenuCategoryResponse::from)
                .toList();
    }

    /**
     * 재료 단가 계산 (2자리 반올림)
     * 1kg, 100g, 1개, 100ml
     * 구매가격 / 구매량 * 기준량
     */
//    private static BigDecimal calUnitPrice(Unit unit, BigDecimal price, BigDecimal amount) {
//        return price.divide(amount, 2, RoundingMode.HALF_UP)
//                .multiply(new BigDecimal(unit.getBaseQuantity()));
//    }
}
