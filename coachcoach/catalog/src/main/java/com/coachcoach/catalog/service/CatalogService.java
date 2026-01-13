package com.coachcoach.catalog.service;

import com.coachcoach.catalog.api.response.*;
import com.coachcoach.catalog.domain.entity.*;
import com.coachcoach.catalog.domain.repository.*;
import com.coachcoach.catalog.global.exception.CatalogErrorCode;
import com.coachcoach.catalog.global.util.Cache;
import com.coachcoach.catalog.global.util.Calculator;
import com.coachcoach.catalog.global.util.CodeFinder;
import com.coachcoach.catalog.api.request.IngredientCreateRequest;
import com.coachcoach.catalog.api.request.IngredientUpdateRequest;
import com.coachcoach.catalog.api.request.MenuCreateRequest;
import com.coachcoach.catalog.api.request.SupplierUpdateRequest;
import com.coachcoach.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CatalogService {

    private final Cache cache;
    private final CodeFinder codeFinder;
    private final Calculator calculator;
    private final IngredientCategoryRepository ingredientCategoryRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private final IngredientRepository ingredientRepository;
    private final MenuRepository menuRepository;
    private final IngredientPriceHistoryRepository ingredientPriceHistoryRepository;
    private final TemplateMenuRepository templateMenuRepository;
    private final TemplateRecipeRepository templateRecipeRepository;
    private final TemplateIngredientRepository templateIngredientRepository;

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
                    .map(x -> IngredientResponse.of(x, codeFinder.findUnitByCode(x.getUnitCode()).getBaseQuantity()))
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
        BigDecimal unitPrice = calculator.calUnitPrice(unit, request.getPrice(), request.getAmount());

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

        return results.stream()
                .map(x -> PriceHistoryResponse.of(
                        x,
                        x.getUnitCode(),
                        codeFinder.findUnitByCode(x.getUnitCode()).getBaseQuantity()
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
        BigDecimal unitPrice = calculator.calUnitPrice(currentUnit,  request.getPrice(), request.getAmount());

        // 가격 변동률 계산 (단위가 바뀐 경우 null로 설정)
        BigDecimal changeRate = (currentUnit.equals(previousUnit)) ? calculator.calChangeRate(currentUnit, ingredient.getCurrentUnitPrice(), unitPrice) : null;

        // 가격 업데이트
        ingredient.update(unitPrice, currentUnit.getUnitCode());

        // 히스토리 업데이트
        IngredientPriceHistory iph = ingredientPriceHistoryRepository.save(
                IngredientPriceHistory.create(
                        ingredientId,
                        unitPrice,
                        currentUnit.getUnitCode(),
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
     * 메뉴명 검색
     */
    public List<SearchMenusResponse> searchMenus(String keyword) {
        List<TemplateMenu> result = templateMenuRepository.findByKeywords(keyword);

        return result.stream()
                .map(SearchMenusResponse::from)
                .toList();
    }

    /**
     * 템플릿에 따른 메뉴 기본 정보 제공 (메뉴명 + 가격 + 카테고리 + 제조시간)
     */
    public TemplateBasicResponse readMenuTemplate(Long templateId) {
        TemplateMenu template = templateMenuRepository.findById(templateId).orElseThrow(() -> new BusinessException(CatalogErrorCode.NOTFOUND_TEMPLATE));

        return TemplateBasicResponse.from(template);
    }

    /**
     * 템플릿에 따른 재료 리스트 제공
     */
    public List<RecipeTemplateResponse> readTemplateIngredients(Long templateId) {
        List<TemplateRecipe> recipes = templateRecipeRepository.findByTemplateIdOrderByRecipeTemplateIdAsc(templateId);

        return recipes.stream()
                .map(x ->
                        RecipeTemplateResponse.of(
                        x,
                        templateIngredientRepository.findById(x.getIngredientTemplateId()).orElseThrow(() -> new BusinessException(CatalogErrorCode.NOTFOUND_INGREDIENT))
                ))
                .toList();
    }

    /**
     * 메뉴 등록
     */
    public void createMenu(Long userId, MenuCreateRequest request) {

    }
}
