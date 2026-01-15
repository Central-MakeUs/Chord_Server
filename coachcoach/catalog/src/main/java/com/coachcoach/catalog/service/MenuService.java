package com.coachcoach.catalog.service;

import com.coachcoach.catalog.api.request.IngredientCreateRequest;
import com.coachcoach.catalog.api.request.MenuCreateRequest;
import com.coachcoach.catalog.api.request.NewRecipeCreateRequest;
import com.coachcoach.catalog.api.request.RecipeCreateRequest;
import com.coachcoach.catalog.api.response.*;
import com.coachcoach.catalog.domain.entity.*;
import com.coachcoach.catalog.domain.repository.*;
import com.coachcoach.catalog.global.exception.CatalogErrorCode;
import com.coachcoach.catalog.global.util.Cache;
import com.coachcoach.catalog.global.util.Calculator;
import com.coachcoach.catalog.global.util.CodeFinder;
import com.coachcoach.common.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
     * todo: 유사도 기반 나열
     */
    public List<SearchMenusResponse> searchMenus(String keyword) {
        List<TemplateMenu> result = templateMenuRepository.findByKeywordWithPriority(keyword);

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
     * 카테고리 별 메뉴 목록 반환 (필터링)
     */
    public List<MenuResponse> readMenusByCategory(Long userId, String categoryCode) {
        // 유효성 검사
        if(!codeFinder.existsMenuCategory(categoryCode)){
            throw new BusinessException(CatalogErrorCode.NOTFOUND_CATEGORY);
        }

        List<Menu> menus = menuRepository.findByUserIdAndMenuCategoryCodeOrderByMenuIdDesc(userId, categoryCode);
        return menus.stream()
                .map(x -> MenuResponse.of(x, codeFinder.findMarginCodeByCode(x.getMarginGradeCode())))
                .toList();
    }

    /**
     * 메뉴 상세 정보 조회
     */
    public MenuDetailResponse readMenu(
            Long userId, Long menuId
    ) {
        Menu menu = menuRepository.findByUserIdAndMenuId(userId, menuId).orElseThrow(() -> new BusinessException(CatalogErrorCode.NOTFOUND_MENU));
        MarginGrade marginGrade = codeFinder.findMarginCodeByCode(menu.getMarginGradeCode());
        return MenuDetailResponse.of(menu, marginGrade);
    }

    /**
     * 메뉴 상세 정보 - 레시피 목록 조회
     */
    public RecipeListResponse readRecipes(
            Long userId,
            Long menuId
    ) {
        // 유효성 검증 (메뉴 존재 여부)
        Menu menu = menuRepository
                .findByUserIdAndMenuId(userId, menuId)
                .orElseThrow(() -> new BusinessException(CatalogErrorCode.NOTFOUND_MENU));

        List<Recipe> recipes = recipeRepository
                .findByMenuIdOrderByRecipeIdAsc(menu.getMenuId());
        List<Long> ingredientIds = recipes.stream()
                .map(Recipe::getIngredientId)
                .toList();

        List<Ingredient> ingredients = ingredientRepository.findByUserIdAndIngredientIdIn(userId, ingredientIds);

        Map<Long, Ingredient> ingredientMap = ingredients.stream()
                .collect(Collectors.toMap(Ingredient::getIngredientId, Function.identity()));


        BigDecimal totalCost = BigDecimal.ZERO;
        List<RecipeResponse> responses = new ArrayList<>();

        for(int i = 0; i < recipes.size(); ++i) {
            Recipe recipe = recipes.get(i);
            Ingredient ingredient = ingredientMap.get(recipe.getIngredientId());

            if(ingredient == null){
                throw new BusinessException(CatalogErrorCode.NOTFOUND_INGREDIENT);
            }

            Unit unit = codeFinder.findUnitByCode(ingredient.getUnitCode());

            BigDecimal price = ingredient.getCurrentUnitPrice()
                    .divide(BigDecimal.valueOf(unit.getBaseQuantity()), 10, RoundingMode.HALF_UP)
                    .multiply(recipe.getAmount())
                    .setScale(2, RoundingMode.HALF_UP);

            totalCost = totalCost.add(price);

            responses.add(
                    RecipeResponse.of(
                            recipe.getRecipeId(),
                            menuId,
                            ingredient.getIngredientId(),
                            ingredient.getIngredientName(),
                            recipe.getAmount(),
                            unit.getUnitCode(),
                            price
                    )
            );
        }

        return RecipeListResponse.of(
                responses,
                totalCost
        );
    }


    /**
     * 메뉴 생성
     */
    @Transactional
    public void createMenu(
            Long userId,
            BigDecimal laborCost,
            MenuCreateRequest request
    ) {
        // 유효성 검증(메뉴 카테고리)
        if(!codeFinder.existsMenuCategory(request.getMenuCategoryCode())) {
            throw new BusinessException(CatalogErrorCode.NOTFOUND_CATEGORY);
        }

        // 메뉴 등록

        // 총 원가 계산
        List<Long> ingredientIds = request.getRecipes().stream()
                .map(RecipeCreateRequest::getIngredientId)
                .toList();
        Map<Long, Ingredient> ingredientMap = ingredientRepository
                .findByUserIdAndIngredientIdIn(userId, ingredientIds)
                .stream()
                .collect(
                        Collectors.toMap(
                                Ingredient::getIngredientId,
                                Function.identity()
                        )
                );

        BigDecimal totalCost = Stream.concat(
                // 기존 재료 이용
                request.getRecipes().stream()
                        .map(recipe -> {
                            Ingredient ingredient = Optional.ofNullable(ingredientMap.get(recipe.getIngredientId()))
                                    .orElseThrow(() -> new BusinessException(CatalogErrorCode.NOTFOUND_INGREDIENT));
                            log.info("price: " + recipe.getAmount().multiply(ingredient.getCurrentUnitPrice()));
                            return recipe.getAmount()
                                    .divide(
                                            BigDecimal.valueOf(codeFinder.findUnitByCode(ingredient.getUnitCode()).getBaseQuantity()), 10, RoundingMode.HALF_UP)
                                    .multiply(ingredient.getCurrentUnitPrice());
                        }),
                request.getNewRecipes()
                        .stream()
                        .map(NewRecipeCreateRequest::getPrice)
        ).reduce(BigDecimal.ZERO, BigDecimal::add);
        log.info("total cost: " + totalCost);

        // analysis 계산
        MenuCostAnalysis analysis = calculator.calAnalysis(
                totalCost,
                request.getSellingPrice(),
                laborCost,
                request.getWorkTime()
        );

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
                )
        );

        // 기존 재료 이용 레시피 등록 (배치)
        List<Recipe> recipes = request.getRecipes().stream()
                .map(
                        recipe -> Recipe.create(
                                menu.getMenuId(),
                                recipe.getIngredientId(),
                                recipe.getAmount())
                )
                .toList();
        if(!recipes.isEmpty()) recipeRepository.saveAll(recipes);

        // 새로운 재료 등록 & 레시피 등록
        createNewIngredientsWithRecipes(
                userId,
                menu.getMenuId(),
                request.getNewRecipes()
        );
    }


    /**
     * 새 재료 & 레시피 배치 생성
     */
    private void createNewIngredientsWithRecipes(
            Long userId,
            Long menuId,
            List<NewRecipeCreateRequest> newRecipes
    ) {

        // 1. 유효성 검증 (배치)
        newRecipes.forEach(recipe -> {
            if (!codeFinder.existsIngredientCategory(recipe.getIngredientCategoryCode())) {
                throw new BusinessException(CatalogErrorCode.NOTFOUND_CATEGORY);
            }
            if (!codeFinder.existsUnit(recipe.getUnitCode())) {
                throw new BusinessException(CatalogErrorCode.NOTFOUND_UNIT);
            }
        });

        // 2. 중복 조회 (배치)
        // todo: 중복 시 (1), (2), ...등으로 처리
        List<String> ingredientNames =
                newRecipes.stream()
                        .map(NewRecipeCreateRequest::getIngredientName)
                        .toList();
        Set<String> existingNames = ingredientRepository
                .findByUserIdAndIngredientNameIn(userId, ingredientNames)
                .stream()
                .map(Ingredient::getIngredientName)
                .collect(Collectors.toSet());

        if (!existingNames.isEmpty()) {
            throw new BusinessException(CatalogErrorCode.DUP_INGREDIENT);
        }

        // 재료
        List<Ingredient> ingredients = newRecipes.stream()
                .map(recipe -> {
                    // 단가 계산
                    Unit unit = codeFinder.findUnitByCode(recipe.getUnitCode());
                    BigDecimal unitPrice = calculator.calUnitPrice(unit, recipe.getPrice(), recipe.getAmount());

                    // 단가 유효성 검증 0.00 이상
                    if(unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
                        throw new BusinessException(CatalogErrorCode.INVALID_UNIT_PRICE);
                    }

                    return Ingredient.create(
                            userId,
                            recipe.getIngredientCategoryCode(),
                            recipe.getIngredientName(),
                            recipe.getUnitCode(),
                            unitPrice,
                            recipe.getSupplier()
                    );
                })
                .toList();

        if(!ingredients.isEmpty()) {
            List<Ingredient> results = ingredientRepository.saveAll(ingredients);

            // 히스토리 & 레시피
            List<IngredientPriceHistory> histories = new ArrayList<>();
            List<Recipe> recipes = new ArrayList<>();
            for(int i = 0; i < ingredients.size(); ++i) {
                Ingredient ingredient = results.get(i);
                NewRecipeCreateRequest recipe = newRecipes.get(i);

                histories.add(
                        IngredientPriceHistory.create(
                                ingredient.getIngredientId(),
                                ingredient.getCurrentUnitPrice(),
                                ingredient.getUnitCode(),
                                recipe.getAmount(),
                                recipe.getPrice(),
                                null
                        ));

                recipes.add(
                        Recipe.create(
                                menuId,
                                ingredient.getIngredientId(),
                                recipe.getAmount()
                        )
                );
            }

            ingredientPriceHistoryRepository.saveAll(histories);
            recipeRepository.saveAll(recipes);
        }
    }

    /**
     * 레시피 추가 (단일 / 기존 재료)
     */
    @Transactional
    public void createRecipe(
            Long userId,
            BigDecimal laborCost,
            Long menuId,
            RecipeCreateRequest request
    ) {
        // 유효성 검증 (메뉴 존재 여부 & 재료 존재 여부)
        Menu menu = menuRepository
                .findByUserIdAndMenuId(userId, menuId)
                .orElseThrow(() -> new BusinessException(CatalogErrorCode.NOTFOUND_MENU));

        if(!ingredientRepository.existsByUserIdAndIngredientId(userId, request.getIngredientId())) {
            throw new BusinessException(CatalogErrorCode.NOTFOUND_INGREDIENT);
        }

        // 중복 확인 (해당 메뉴에 이미 해당 재료로 레시피가 존재하는지)
        if(recipeRepository.existsByMenuIdAndIngredientId(menuId, request.getIngredientId())) {
            throw new BusinessException(CatalogErrorCode.DUP_RECIPE);
        }

        // 레시피 등록
        Recipe recipe = recipeRepository.save(
                Recipe.create(
                        menuId,
                        request.getIngredientId(),
                        request.getAmount()
                )
        );

        // analysis 재계산 + 업데이트
        // 총 원가 재계산
        List<Recipe> recipes = recipeRepository.findByMenuId(menuId);

        List<Long> ingredientIds = recipes.stream()
                .map(Recipe::getIngredientId)
                .toList();
        Map<Long, Ingredient> ingredientMap = ingredientRepository.findByUserIdAndIngredientIdIn(userId, ingredientIds).stream()
                .collect(Collectors.toMap(Ingredient::getIngredientId, Function.identity()));

        BigDecimal totalCost = recipes.stream()
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
                .reduce(BigDecimal.ZERO, BigDecimal::add);

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
    }

    /**
     * 메뉴명 수정
     */
    @Transactional
    public void updateMenuName(
            Long userId, Long menuId, String menuName
    ) {
        // 유효성 검사
        Menu menu = menuRepository
                .findByUserIdAndMenuId(userId, menuId)
                .orElseThrow(() -> new BusinessException(CatalogErrorCode.NOTFOUND_MENU));

        menu.updateName(menuName);
    }

    /**
     * 카테고리 수정
     */
    @Transactional
    public void updateMenuCategory(
            Long userId, Long menuId, String category
    ) {
        // 유효성 검사
        Menu menu = menuRepository
                .findByUserIdAndMenuId(userId, menuId)
                .orElseThrow(() -> new BusinessException(CatalogErrorCode.NOTFOUND_MENU));

        menu.updateCategory(category);
    }

    /**
     * 메뉴 삭제 (단일)
     */
    @Transactional
    public void deleteMenu(
            Long userId,
            Long menuId
    ) {
        // 유효성 검증 (존재하는 메뉴인지)
        Menu menu = menuRepository
                .findByUserIdAndMenuId(userId, menuId)
                .orElseThrow(() -> new BusinessException(CatalogErrorCode.NOTFOUND_MENU));

        // 레시피 삭제 (배치)
        recipeRepository.deleteByMenuId(menuId);

        // 메뉴 삭제
        menuRepository.delete(menu);
    }
}
