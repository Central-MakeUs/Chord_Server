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
public class MenuService {

    private final Cache cache;
    private final CodeFinder codeFinder;
    private final Calculator calculator;
    private final IngredientRepository ingredientRepository;
    private final MenuRepository menuRepository;
    private final IngredientPriceHistoryRepository ingredientPriceHistoryRepository;
    private final RecipeRepository recipeRepository;
    private final TemplateMenuRepository templateMenuRepository;
    private final TemplateRecipeRepository templateRecipeRepository;
    private final TemplateIngredientRepository templateIngredientRepository;

    private final IngredientService ingredientService;

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
                        ingredient = ingredientService.updateIngredientReturnedEntity(userId, laborCost, x.getIngredientId(), IngredientUpdateRequest.of(x.getPrice(), x.getAmount(), x.getUnitCode()));
                    } else {
                        // 새로운 재료 등록
                        ingredient = ingredientService.createIngredientReturnedEntity(userId, IngredientCreateRequest.of(
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

    /**
     * 카테고리 별 메뉴 목록 반환 (필터링)
     */
    public List<MenuResponse> readMenusByCategory(Long userId, String categoryCode) {
        // 유효성 검사
        if(!codeFinder.existsMenuCategory(categoryCode)){
            throw new BusinessException(CatalogErrorCode.NOTFOUND_CATEGORY);
        }

        List<Menu> menus = menuRepository.findByUserIdAndMenuCategoryCodeOrderByMenuIdDesc(userId, categoryCode);
        return menus.stream()
                .map(x -> MenuResponse.of(x, codeFinder.getMarginNameByCode(x.getMarginGradeCode())))
                .toList();
    }

    /**
     * 메뉴 상세 정보 반환
     */
    public MenuDetailResponse readMenu(
            Long userId, Long menuId
    ) {
        Menu menu = menuRepository.findByUserIdAndMenuId(userId, menuId).orElseThrow(() -> new BusinessException(CatalogErrorCode.NOTFOUND_MENU));
        MarginGrade margin = codeFinder.findMarginCodeByCode(menu.getMarginGradeCode());
        return MenuDetailResponse.of(menu, margin.getGradeName(), margin.getMessage());
    }
}
