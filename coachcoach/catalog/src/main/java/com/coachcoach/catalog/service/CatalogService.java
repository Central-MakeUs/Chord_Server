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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private final RecipeRepository recipeRepository;
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

    @Transactional
    public Ingredient createIngredientReturnedEntity(Long userId, IngredientCreateRequest request) {
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

        return ingredient;
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
     * 재료 단가 수정에 따른 해당 재료 포함 메뉴 모두 변동
     */
    private void updateAllMenusByIngredient(Long userId, Long ingredientId, BigDecimal newUnitPrice, BigDecimal laborCost) {

        List<Recipe> recipes = recipeRepository.findByIngredientId(ingredientId);
        recipes.forEach(x -> {
            // 레시피 수정 (메뉴 당 1개) → cost 업데이트
            BigDecimal newCost = newUnitPrice.multiply(x.getUsageAmount()).setScale(2, RoundingMode.HALF_UP);
            BigDecimal privCost = x.getCost();

            x.update(newCost);

            // 메뉴 수정(총 원가, 원가율, 공헌이익률, 마진율, 마진 등급 코드, 권장 가격)
            Menu menu = menuRepository.findByUserIdAndMenuId(userId, x.getMenuId()).orElseThrow(() -> new BusinessException(CatalogErrorCode.NOTFOUND_MENU));

            BigDecimal totalCost = menu.getTotalCost().subtract(privCost).add(newCost);

            MenuCostAnalysis analysis = calculator.calAnalysis(
                    totalCost,
                    menu.getSellingPrice(),
                    laborCost,
                    menu.getWorkTime()
            );

            menu.update(
                    totalCost,
                    analysis.getCostRate(),
                    analysis.getContributionMargin(),
                    analysis.getMarginRate(),
                    analysis.getMarginGradeCode(),
                    analysis.getRecommendedPrice()
            );
        });
    }

    /**
     * 재료 단가 수정
     */
    @Transactional
    public IngredientUpdateResponse updateIngredient(Long userId, BigDecimal laborCost, Long ingredientId, IngredientUpdateRequest request) {
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

        // 가격 업데이트에 따른 메뉴들 수정
        updateAllMenusByIngredient(userId, ingredientId, unitPrice, laborCost);

        // 변환
        return IngredientUpdateResponse.of(
                unitPrice,
                currentUnit.getBaseQuantity(),
                currentUnit.getUnitCode(),
                iph
        );
    }

    @Transactional
    public Ingredient updateIngredientReturnedEntity(Long userId, BigDecimal laborCost, Long ingredientId, IngredientUpdateRequest request) {
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

        // 가격 업데이트에 따른 메뉴들 수정
        updateAllMenusByIngredient(userId, ingredientId, unitPrice, laborCost);

        // 변환
        return ingredient;
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
     * 레시피 등록
     */
    private IngredientResponse createRecipe(Long userId, Long menuId, Ingredient ingredient, BigDecimal amount, BigDecimal cost) {
        // 유효성 검사
        if(!menuRepository.existsByUserIdAndMenuId(userId, menuId)) {
            throw new BusinessException(CatalogErrorCode.NOTFOUND_MENU);
        } else if(recipeRepository.existsByMenuIdAndIngredientId(menuId, ingredient.getIngredientId())) {
            // 해당 레시피에 이미 해당 재료가 존재하는 경우
            throw new BusinessException(CatalogErrorCode.DUP_INGREDIENT);
        }

        Recipe recipe = recipeRepository.save(
                Recipe.create(menuId, ingredient.getIngredientId(), amount, cost)
        );

        return IngredientResponse.of(
                ingredient,
                codeFinder.findUnitByCode(ingredient.getUnitCode()).getBaseQuantity()
        );
    }

    /**
     * 메뉴 등록
     */
    @Transactional
    public void createMenu(Long userId, BigDecimal laborCost, MenuCreateRequest request) {
        // 유효성 검사
        if(!codeFinder.existsMenuCategory(request.getMenuCategoryCode())) {
            throw new BusinessException(CatalogErrorCode.NOTFOUND_CATEGORY);
        } else  if(menuRepository.existsByUserIdAndMenuName(userId, request.getMenuName())) {
            // 메뉴명 중복 불가
            throw  new BusinessException(CatalogErrorCode.DUP_MENU);
        }

        // 총 원가 계산
        List<BigDecimal> costs = request.getIngredients().stream()
                .map(MenuCreateRequest.IngredientRequest::getPrice)
                .toList();

        BigDecimal totalCost = calculator.calTotalCost(costs);

        MenuCostAnalysis analysis = calculator.calAnalysis(
                totalCost,
                request.getSellingPrice(),
                laborCost,
                request.getWorkTime()
        );

        // 메뉴 등록
        Menu menu = menuRepository.save(
                Menu.create(
                userId,
                request.getMenuCategoryCode(),
                request.getMenuName(),
                request.getSellingPrice(),
                totalCost,
                analysis.getCostRate(),
                analysis.getContributionMargin(),
                analysis.getMarginRate(),
                analysis.getMarginGradeCode(),
                request.getWorkTime(),
                analysis.getRecommendedPrice()
        ));

        //재료 단가 계산 & 등록/수정
        request.getIngredients()
                .forEach(x -> {
                    Ingredient ingredient = null;

                    if(x.getIngredientId() != null) {
                        //기존 재료 수정
                        ingredient = updateIngredientReturnedEntity(userId, laborCost, x.getIngredientId(), IngredientUpdateRequest.of(x.getPrice(), x.getAmount(), x.getUnitCode()));
                    } else {
                        // 새로운 재료 등록
                        ingredient = createIngredientReturnedEntity(userId, IngredientCreateRequest.of(
                                x.getCategoryCode(),
                                x.getIngredientName(),
                                x.getUnitCode(),
                                x.getPrice(),
                                x.getAmount(),
                                x.getSupplier()
                        ));

                    }

                    //레시피 등록
                    createRecipe(userId, menu.getMenuId(), ingredient, x.getAmount(), x.getPrice());
                });
    }
}
