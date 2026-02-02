package com.coachcoach.catalog.service;

import com.coachcoach.catalog.domain.*;
import com.coachcoach.catalog.dto.request.IngredientCreateRequest;
import com.coachcoach.catalog.dto.request.IngredientUpdateRequest;
import com.coachcoach.catalog.dto.request.SupplierUpdateRequest;
import com.coachcoach.catalog.dto.response.*;
import com.coachcoach.catalog.exception.CatalogErrorCode;
import com.coachcoach.catalog.util.Cache;
import com.coachcoach.catalog.util.Calculator;
import com.coachcoach.catalog.util.CodeFinder;
import com.coachcoach.catalog.util.DuplicateNameResolver;
import com.coachcoach.catalog.repository.*;
import com.coachcoach.common.api.UserQueryApi;
import com.coachcoach.common.dto.internal.StoreInfo;
import com.coachcoach.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IngredientService {
    private final Cache cache;
    private final CodeFinder codeFinder;
    private final Calculator calculator;
    private final IngredientCategoryRepository ingredientCategoryRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private final IngredientRepository ingredientRepository;
    private final MenuRepository menuRepository;
    private final IngredientPriceHistoryRepository ingredientPriceHistoryRepository;
    private final RecipeRepository recipeRepository;
    private final TemplateMenuRepository templateMenuRepository;
    private final TemplateRecipeRepository templateRecipeRepository;
    private final TemplateIngredientRepository templateIngredientRepository;
    private final DuplicateNameResolver nameResolver;
    private final UserQueryApi userQueryApi;

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
        if(category == null || category.isEmpty()) {
            // 전체 조회
            return ingredientRepository.findAllByUserIdOrderByIngredientIdDesc(userId)
                    .stream()
                    .map(x -> IngredientResponse.of(
                            x, codeFinder.findUnitByCode(x.getUnitCode())
                    ))
                    .toList();
        }

        // 카테고리 유효성 검사
        boolean includeFavorite = category.contains("FAVORITE");
        List<String> actualCategories = category.stream()
                .filter(code -> !"FAVORITE".equals(code))
                .toList();

        if (actualCategories.stream()
                .anyMatch(code -> !codeFinder.existsIngredientCategory(code))) {
            throw new BusinessException(CatalogErrorCode.NOTFOUND_CATEGORY);
        }

        return (includeFavorite ?
                ingredientRepository.findByUserIdAndCategoryCodesOrFavorite(userId, category) :
                ingredientRepository.findByUserIdAndIngredientCategoryCodeInOrderByIngredientIdDesc(userId, category))
                .stream()
                .map(x -> IngredientResponse.of(x, codeFinder.findUnitByCode(x.getUnitCode())))
                .toList();

    }

    /**
     * 재료 상세 조회
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
                unit,
                menus,
                iph
        );
    }

    /**
     * 가격 변경 이력 목록 조회
     */
    public List<PriceHistoryResponse> readIngredientPriceHistory(Long userId, Long ingredientId) {
        // 변경 이력 조회
        List<IngredientPriceHistory> results = ingredientPriceHistoryRepository.findByIngredientIdOrderByHistoryIdDesc(ingredientId);

        // 단위 조회
        return results.stream()
                .map(x -> PriceHistoryResponse.of(
                        x,
                        codeFinder.findUnitByCode(x.getUnitCode())
                ))
                .toList();
    }

    /**
     * 재료 검색 (in template & users)
     * 정렬 기준
     * 1. 템플릿 우선
     * 2. 같은 템플릿/유저목록 내에서는 유사도 순 나열 & ingredientName 오름차순
     */
    public List<SearchIngredientsResponse> searchIngredients(Long userId, String keyword) {
        if(keyword == null || keyword.isBlank()) {
            return Collections.emptyList();
        }

        List<TemplateIngredient> templates = templateIngredientRepository.findByKeywordOrderByIngredientNameAsc(keyword);
        List<Ingredient> ingredients = ingredientRepository.findByUserIdAndKeywordOrderByIngredientNameAsc(userId, keyword);

        List<SearchIngredientsResponse> response = new ArrayList<>();

        templates.forEach(template -> {
            response.add(
                    SearchIngredientsResponse.from(template)
            );
        });

        ingredients.forEach(ingredient -> {
            response.add(
                    SearchIngredientsResponse.from(ingredient)
            );
        });;

        return response;
    }

    /**
     * 재료명 중복 확인
     */
    public void checkDupIngredientName(Long userId, String ingredientName) {
        if(ingredientRepository.existsByUserIdAndIngredientName(userId, ingredientName)) {
            throw new BusinessException(CatalogErrorCode.DUP_INGREDIENT);
        }
    }

    /**
     * 재료 검색 (with 재료명, 메뉴명)
     */
    public List<SearchMyIngredientsResponse> searchMyIngredients(Long userId, String keyword) {
        if(keyword == null || keyword.isBlank()) {
            return Collections.emptyList();
        }

        List<Ingredient> ingredients = ingredientRepository.findByUserIdAndMenuNameAndIngredientNameOrderByIngredientNameAsc(userId, keyword);

        return ingredients.stream()
                .map(SearchMyIngredientsResponse::from)
                .toList();
    }

    /**
     * 재료 생성(재료명, 가격, 사용량, 단위, 카테고리)
     */
    @Transactional(transactionManager = "catalogTransactionManager")
    public IngredientResponse createIngredient(Long userId, IngredientCreateRequest request) {
        // 재료 카테고리 & 유닛 유효성 검증
        if(!codeFinder.existsIngredientCategory(request.categoryCode())) {
            throw new BusinessException(CatalogErrorCode.NOTFOUND_CATEGORY);
        } else if(!codeFinder.existsUnit(request.unitCode())) {
            throw new BusinessException(CatalogErrorCode.NOTFOUND_UNIT);
        }

        // 중복 확인 (userId + ingredientName)
        String ingredientName = nameResolver.createNonDupIngredientName(userId, request.ingredientName());

        // 단가 계산
        Unit unit = codeFinder.findUnitByCode(request.unitCode());
        BigDecimal unitPrice = calculator.calUnitPrice(unit, request.price(), request.amount());

        // 단가 유효성 검증 0.00 이상
        if(unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(CatalogErrorCode.INVALID_UNIT_PRICE);
        }

        // 재료 단가 입력
        Ingredient ingredient = ingredientRepository.save(
                Ingredient.create(
                        userId,
                        request.categoryCode(),
                        ingredientName,
                        request.unitCode(),
                        unitPrice,
                        request.supplier()
                ));

        // 히스토리 입력
        IngredientPriceHistory ingredientPriceHistory = ingredientPriceHistoryRepository.save(
                IngredientPriceHistory.create(
                        ingredient.getIngredientId(),
                        unitPrice,
                        unit.getUnitCode(),
                        request.amount(),
                        request.price(),
                        null
                ));

        return IngredientResponse.of(ingredient, unit);
    }

    /**
     * 즐겨찾기 설정/해제
     */
    @Transactional(transactionManager = "catalogTransactionManager")
    public void updateFavorite(Long userId, Long ingredientId, Boolean favorite) {
        Ingredient ingredient = ingredientRepository.findByUserIdAndIngredientId(userId, ingredientId).orElseThrow(() -> new BusinessException(CatalogErrorCode.NOTFOUND_INGREDIENT));
        ingredient.updateFavorite(favorite);
    }

    /**
     * 재료 공급업체 수정
     */
    @Transactional(transactionManager = "catalogTransactionManager")
    public void updateIngredientSupplier(Long userId, Long ingredientId, SupplierUpdateRequest request) {
        Ingredient ingredient = ingredientRepository.findByUserIdAndIngredientId(userId, ingredientId).orElseThrow(() -> new BusinessException(CatalogErrorCode.NOTFOUND_INGREDIENT));
        ingredient.updateSupplier(request.supplier());
    }

    /**
     * 재료 단가 수정 -> 해당 재료 사용하는 모든 메뉴에 대해 업데이트 필요
     */
    @Transactional(transactionManager = "catalogTransactionManager")
    public void updateIngredientPrice(
            Long userId, Long ingredientId, IngredientUpdateRequest request
    ) {
        Ingredient ingredient = ingredientRepository.findByUserIdAndIngredientId(userId, ingredientId).orElseThrow(() -> new BusinessException(CatalogErrorCode.NOTFOUND_INGREDIENT));

        StoreInfo storeInfo = userQueryApi.findStoreByUserId(userId);
        BigDecimal laborCost = storeInfo.laborCost();

        // 카테고리 유효성 검증
        if(!codeFinder.existsIngredientCategory(request.category())) {
            throw new BusinessException(CatalogErrorCode.NOTFOUND_CATEGORY);
        }

        Unit previousUnit = codeFinder.findUnitByCode(ingredient.getUnitCode());
        Unit currentUnit = codeFinder.findUnitByCode(request.unitCode());

        // 단가/변동률 계산 + 재료 업데이트
        BigDecimal unitPrice = calculator.calUnitPrice(currentUnit, request.price(), request.amount());

        // 단가 유효성 검증 0.00 이상
        if(unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(CatalogErrorCode.INVALID_UNIT_PRICE);
        }

        BigDecimal changeRate = (currentUnit.equals(previousUnit)) ? calculator.calChangeRate(currentUnit, ingredient.getCurrentUnitPrice(), unitPrice) : null;

        ingredient.update(request.category(), unitPrice, request.unitCode());

        // 히스토리 업데이트
        IngredientPriceHistory history = ingredientPriceHistoryRepository.save(
                IngredientPriceHistory.create(
                        ingredient.getIngredientId(),
                        ingredient.getCurrentUnitPrice(),
                        currentUnit.getUnitCode(),
                        request.amount(),
                        request.price(),
                        changeRate
                )
        );

        // 해당 재료 사용하는 모든 메뉴 정보 수정

        // 레시피 테이블에서 ingredientId로 menuId 목록 조회
        List<Recipe> recipesToUpdate = recipeRepository.findByIngredientId(ingredientId);

        // 목록 순회 -> 메뉴마다 레시피 목록 조회 -> analysis 재계산 + 업데이트 (배치)
        recipesToUpdate.forEach(recipe -> {
            Menu menu = menuRepository
                    .findByUserIdAndMenuId(userId, recipe.getMenuId())
                    .orElseThrow(() -> new BusinessException(CatalogErrorCode.NOTFOUND_MENU));

            BigDecimal totalCost = calculator.calTotalCostWithRecipes(
                    userId,
                    recipeRepository.findByMenuId(recipe.getMenuId())
            );

            MenuCostAnalysis analysis = calculator.calAnalysis(
                    totalCost,
                    menu.getSellingPrice(),
                    laborCost,
                    menu.getWorkTime()
            );

            log.info("update menu " + menu.getMenuName());

            menu.update(
                    totalCost,
                    analysis.costRate(),
                    analysis.contributionMargin(),
                    analysis.marginRate(),
                    analysis.marginGradeCode(),
                    analysis.recommendedPrice()
            );
        });
    }

    /**
     * 재료 삭제 -> 해당 재료 사용하는 모든 메뉴 업데이트 필요
     */
    @Transactional(transactionManager = "catalogTransactionManager")
    public void deleteIngredient(
            Long userId, Long ingredientId
    ) {
        Ingredient ingredient = ingredientRepository.findByUserIdAndIngredientId(userId, ingredientId).orElseThrow(() -> new BusinessException(CatalogErrorCode.NOTFOUND_INGREDIENT));

        StoreInfo storeInfo = userQueryApi.findStoreByUserId(userId);
        BigDecimal laborCost = storeInfo.laborCost();

        // 해당 재료 포함 레시피 모두 삭제
        List<Recipe> recipesToUpdate = recipeRepository.findByIngredientId(ingredientId);
        recipeRepository.deleteAll(recipesToUpdate);

        // 히스토리 + 재료 삭제
        List<IngredientPriceHistory> histories = ingredientPriceHistoryRepository.findByIngredientId(ingredientId);
        ingredientPriceHistoryRepository.deleteAll(histories);

        ingredientRepository.delete(ingredient);

        // 메뉴 정보 일괄 업데이트
        recipesToUpdate.forEach(recipe -> {
            Menu menu = menuRepository
                    .findByUserIdAndMenuId(userId, recipe.getMenuId())
                    .orElseThrow(() -> new BusinessException(CatalogErrorCode.NOTFOUND_MENU));

            BigDecimal totalCost = calculator.calTotalCostWithRecipes(
                    userId,
                    recipeRepository.findByMenuId(recipe.getMenuId())
            );

            log.info("이전 총 원가: " + menu.getTotalCost() + " 변경된 총 원가: " + totalCost);
            MenuCostAnalysis analysis = calculator.calAnalysis(
                    totalCost,
                    menu.getSellingPrice(),
                    laborCost,
                    menu.getWorkTime()
            );

            log.info("update menu: " + menu.getMenuName());

            menu.update(
                    totalCost,
                    analysis.costRate(),
                    analysis.contributionMargin(),
                    analysis.marginRate(),
                    analysis.marginGradeCode(),
                    analysis.recommendedPrice()
            );
        });
    }
}
