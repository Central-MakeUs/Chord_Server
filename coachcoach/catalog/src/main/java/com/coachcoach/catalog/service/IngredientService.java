package com.coachcoach.catalog.service;

import com.coachcoach.catalog.api.request.IngredientCreateRequest;
import com.coachcoach.catalog.api.request.SupplierUpdateRequest;
import com.coachcoach.catalog.api.response.*;
import com.coachcoach.catalog.domain.entity.*;
import com.coachcoach.catalog.domain.repository.*;
import com.coachcoach.catalog.global.exception.CatalogErrorCode;
import com.coachcoach.catalog.global.util.Cache;
import com.coachcoach.catalog.global.util.Calculator;
import com.coachcoach.catalog.global.util.CodeFinder;
import com.coachcoach.catalog.global.util.DuplicateNameResolver;
import com.coachcoach.common.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
    public List<SearchIngredientsResponse> searchIngredients(String keyword) {
        List<TemplateIngredient> templates = templateIngredientRepository.findByKeywordOrderByIngredientNameAsc(keyword);
        List<Ingredient> ingredients = ingredientRepository.findByKeywordOrderByIngredientNameAsc(keyword);

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
        request.setIngredientName(nameResolver.createNonDupIngredientName(userId, request.getIngredientName()));
        if(ingredientRepository.existsByUserIdAndIngredientName(userId, request.getIngredientName())) {
            throw new BusinessException(CatalogErrorCode.DUP_INGREDIENT);
        }

        // 단가 계산
        Unit unit = codeFinder.findUnitByCode(request.getUnitCode());
        BigDecimal unitPrice = calculator.calUnitPrice(unit, request.getPrice(), request.getAmount());

        // 단가 유효성 검증 0.00 이상
        if(unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(CatalogErrorCode.INVALID_UNIT_PRICE);
        }

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
                        unit.getUnitCode(),
                        request.getAmount(),
                        request.getPrice(),
                        null
                ));

        return IngredientResponse.of(ingredient, unit);
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
     * 재료 공급업체 수정
     */
    @Transactional
    public void updateIngredientSupplier(Long userId, Long ingredientId, SupplierUpdateRequest request) {
        Ingredient ingredient = ingredientRepository.findByUserIdAndIngredientId(userId, ingredientId).orElseThrow(() -> new BusinessException(CatalogErrorCode.NOTFOUND_INGREDIENT));
        ingredient.updateSupplier(request.getSupplier());
    }
}
