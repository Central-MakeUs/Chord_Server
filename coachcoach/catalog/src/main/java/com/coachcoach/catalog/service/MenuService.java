package com.coachcoach.catalog.service;

import com.coachcoach.catalog.domain.*;
import com.coachcoach.catalog.dto.request.*;
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
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final DuplicateNameResolver nameResolver;
    private final ConversionService conversionService;
    private final UserQueryApi userQueryApi;

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
        if(keyword == null || keyword.isBlank()) {
            return Collections.emptyList();
        }

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
    public List<MenuResponse> readMenusByCategory(Long userId, String category) {
        // null이면 전체 조회
        if(category == null || category.isBlank()) {
            List<Menu> menus = menuRepository.findByUserIdOrderByMenuIdDesc(userId);

            return menus.stream()
                    .map(x -> MenuResponse.of(x, codeFinder.findMarginCodeByCode(x.getMarginGradeCode())))
                    .toList();
        }

        // 유효성 검사
        if(!codeFinder.existsMenuCategory(category)){
            throw new BusinessException(CatalogErrorCode.NOTFOUND_CATEGORY);
        }

        List<Menu> menus = menuRepository.findByUserIdAndMenuCategoryCodeOrderByMenuIdDesc(userId, category);
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
                    new RecipeResponse(
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

        return new RecipeListResponse(
                responses,
                totalCost
        );
    }

    /**
     * 메뉴명 + 재료명 중복 확인 (일괄)
     */
//    @Transactional(transactionManager = "transactionManager")
//    public CheckDupResponse checkDupNames(Long userId, CheckDupRequest request) {
//        // 메뉴명 중복 확인
//        Boolean menuNameDuplicate = menuRepository.existsByUserIdAndMenuName(userId, request.menuName());
//
//        // 재료명 중복 확인
//        List<String> dupIngredientNames = ingredientRepository
//                .findByUserIdAndIngredientNameIn(userId, request.ingredientNames())
//                .stream()
//                .map(Ingredient::getIngredientName)
//                .toList();
//
//        return new CheckDupResponse(menuNameDuplicate, dupIngredientNames);
//    }

    /**
     * 메뉴명 중복 확인
     */
    @Transactional(transactionManager = "transactionManager")
    public void checkDupNames(Long userId, String name) {
        if(menuRepository.existsByUserIdAndMenuName(userId, name)) {
            throw new BusinessException(CatalogErrorCode.DUP_MENU);
        }
    }
    /**
     * 메뉴 생성
     */
    @Transactional(transactionManager = "transactionManager")
    public void createMenu(
            Long userId,
            MenuCreateRequest request
    ) {
        // 유효성 검증(메뉴 카테고리)
        if(!codeFinder.existsMenuCategory(request.menuCategoryCode())) {
            throw new BusinessException(CatalogErrorCode.NOTFOUND_CATEGORY);
        }

        StoreInfo storeInfo = userQueryApi.findStoreByUserId(userId);
        BigDecimal laborCost = calculator.calLaborCost(storeInfo.includeWeeklyHolidayPay(), storeInfo.laborCost());

        // 메뉴 등록
        // 메뉴 이름 중복 확인
        String menuName = nameResolver.createNonDupMenuName(userId, request.menuName());

        // 총 원가 계산

        /* 기존 재료 원가 합 계산 */
        List<Long> ingredientIds = request.recipes().stream()
                .map(RecipeCreateRequest::ingredientId)
                .toList();

        // 기존 재료 이용 등록 시 중복되는 재료 Id 확인
        if(ingredientIds.size() != new HashSet<>(ingredientIds).size()) {
            throw new BusinessException(CatalogErrorCode.DUP_INGREDIENT);
        }

        // <id, Ingredient> - 단가 * 사용량 계산용
        Map<Long, Ingredient> ingredientMap = ingredientRepository
                .findByUserIdAndIngredientIdIn(userId, ingredientIds)
                .stream()
                .collect(
                        Collectors.toMap(
                                Ingredient::getIngredientId,
                                Function.identity()
                        )
                );
        BigDecimal costByExisting = request.recipes().stream()
                .map(recipe -> {
                    Ingredient ingredient = Optional.ofNullable(ingredientMap.get(recipe.ingredientId()))
                            .orElseThrow(() -> new BusinessException(CatalogErrorCode.NOTFOUND_INGREDIENT));

                    return recipe.amount()
                            .divide(
                                    BigDecimal.valueOf(codeFinder.findUnitByCode(ingredient.getUnitCode()).getBaseQuantity()), 10, RoundingMode.HALF_UP
                            ).multiply(ingredient.getCurrentUnitPrice());
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        /* 새 재료 원가 합 (단가 * 사용량) */

        // 새 재료 단가 계산
        List<NewRecipe> newRecipesWithUnitPrice = request.newRecipes().stream()
                .map(recipe -> {
                    // 유효성 검증
                    if(!codeFinder.existsIngredientCategory(recipe.ingredientCategoryCode())) {
                        throw new BusinessException(CatalogErrorCode.NOTFOUND_CATEGORY);
                    }
//                    if(recipe.amount().compareTo(recipe.usageAmount()) < 0) {
//                        throw new BusinessException(CatalogErrorCode.INVALID_USAGE_AMOUNT);
//                    }
                    Unit unit = codeFinder.findUnitByCode(recipe.unitCode());
                    BigDecimal unitPrice = calculator.calUnitPrice(unit, recipe.price(), recipe.amount());

                    return NewRecipe.builder()
                            .amount(recipe.amount())
                            .usageAmount(recipe.usageAmount())
                            .price(recipe.price())
                            .unit(unit)
                            .ingredientCategoryCode(recipe.ingredientCategoryCode())
                            .ingredientName(recipe.ingredientName())
                            .supplier(recipe.supplier())
                            .unitPrice(unitPrice)
                            .build();
                })
                .toList();

        // 새 재료끼리 재료명이 동일한 경우 처리
        List<String> newIngredientNames = request.newRecipes().stream()
                .map(NewRecipeCreateRequest::ingredientName)
                .toList();

        if(newIngredientNames.size() != new HashSet<>(newIngredientNames).size()) {
            throw new BusinessException(CatalogErrorCode.DUP_INGREDIENT);
        }

        // 원가 합 계산
        BigDecimal costByNew = newRecipesWithUnitPrice.stream()
                .map(recipe -> recipe.usageAmount()
                        .divide(BigDecimal.valueOf(recipe.unit().getBaseQuantity()), 10, RoundingMode.HALF_UP)
                        .multiply(recipe.unitPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCost = costByNew.add(costByExisting);

        log.info("메뉴 등록 - 총 원가: " + totalCost);

        /* analysis 계산 */
        MenuCostAnalysis analysis = calculator.calAnalysis(
                totalCost,
                request.sellingPrice(),
                laborCost,
                request.workTime()
        );

        /* 메뉴 등록 */
        Menu menu = menuRepository.save(
                Menu.create(
                        userId,
                        request.menuCategoryCode(),
                        menuName,
                        request.sellingPrice(),
                        totalCost,
                        analysis.costRate(),
                        analysis.contributionMargin(),
                        analysis.marginRate(),
                        analysis.marginGradeCode(),
                        request.workTime(),
                        analysis.recommendedPrice()
                )
        );

        // 기존 재료 이용 레시피 등록 (배치)
        List<Recipe> recipes = request.recipes().stream()
                .map(
                        recipe -> Recipe.create(
                                menu.getMenuId(),
                                recipe.ingredientId(),
                                recipe.amount())
                )
                .toList();
        if(!recipes.isEmpty()) recipeRepository.saveAll(recipes);

        // 새로운 재료 등록 & 레시피 등록
        createNewIngredientsWithRecipes(
                userId,
                menu.getMenuId(),
                newRecipesWithUnitPrice
        );
    }


    /**
     * 새 재료 & 레시피 배치 생성
     */
    private void createNewIngredientsWithRecipes(
            Long userId,
            Long menuId,
            List<NewRecipe> newRecipes
    ) {
        // 재료명 중복 해결
        List<NewRecipe> resolvedRecipes = newRecipes.stream()
                .map(recipe -> {
                    String resolvedName = nameResolver.createNonDupIngredientName(
                            userId,
                            recipe.ingredientName()
                    );
                    return recipe.withIngredientName(resolvedName);
                })
                .toList();


        // 재료
        List<Ingredient> ingredients = resolvedRecipes.stream()
                .map(recipe ->
                    Ingredient.create(
                            userId,
                            recipe.ingredientCategoryCode(),
                            recipe.ingredientName(),
                            recipe.unit().getUnitCode(),
                            recipe.unitPrice(),
                            recipe.supplier()
                    )
                )
                .toList();

        if(!ingredients.isEmpty()) {
            List<Ingredient> results = ingredientRepository.saveAll(ingredients);

            // 히스토리 & 레시피
            List<IngredientPriceHistory> histories = new ArrayList<>();
            List<Recipe> recipes = new ArrayList<>();
            for(int i = 0; i < ingredients.size(); ++i) {
                Ingredient ingredient = results.get(i);
                NewRecipe recipe = resolvedRecipes.get(i);

                histories.add(
                        IngredientPriceHistory.create(
                                ingredient.getIngredientId(),
                                ingredient.getCurrentUnitPrice(),
                                ingredient.getUnitCode(),
                                recipe.amount(),
                                recipe.price(),
                                null
                        ));

                recipes.add(
                        Recipe.create(
                                menuId,
                                ingredient.getIngredientId(),
                                recipe.usageAmount()
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
    @Transactional(transactionManager = "transactionManager")
    public void createRecipe(
            Long userId,
            Long menuId,
            RecipeCreateRequest request
    ) {
        // 유효성 검증 (메뉴 존재 여부 & 재료 존재 여부)
        Menu menu = menuRepository
                .findByUserIdAndMenuId(userId, menuId)
                .orElseThrow(() -> new BusinessException(CatalogErrorCode.NOTFOUND_MENU));

        if(!ingredientRepository.existsByUserIdAndIngredientId(userId, request.ingredientId())) {
            throw new BusinessException(CatalogErrorCode.NOTFOUND_INGREDIENT);
        }

        StoreInfo storeInfo = userQueryApi.findStoreByUserId(userId);
        BigDecimal laborCost = calculator.calLaborCost(storeInfo.includeWeeklyHolidayPay(), storeInfo.laborCost());

        // 중복 확인 (해당 메뉴에 이미 해당 재료로 레시피가 존재하는지)
        if(recipeRepository.existsByMenuIdAndIngredientId(menuId, request.ingredientId())) {
            throw new BusinessException(CatalogErrorCode.DUP_RECIPE);
        }

        // 레시피 등록
        Recipe recipe = recipeRepository.save(
                Recipe.create(
                        menuId,
                        request.ingredientId(),
                        request.amount()
                )
        );

        // analysis 재계산 + 업데이트
        // 총 원가 재계산
        List<Recipe> recipes = recipeRepository.findByMenuId(menuId);

        BigDecimal totalCost = calculator.calTotalCostWithRecipes(
                userId,
                recipes
        );

        MenuCostAnalysis analysis = calculator.calAnalysis(
                totalCost,
                menu.getSellingPrice(),
                laborCost,
                menu.getWorkTime()
        );

        menu.update(
                totalCost,
                analysis.costRate(),
                analysis.contributionMargin(),
                analysis.marginRate(),
                analysis.marginGradeCode(),
                analysis.recommendedPrice()
        );
    }

    /**
     * 레시피 추가 (단일 / 새 재료)
     */
    @Transactional(transactionManager = "transactionManager")
    public void createRecipeWithNew(
            Long userId,
            Long menuId,
            NewRecipeCreateRequest request
    ) {
        // 유효성 검증 (메뉴 존재 여부 & 재료 카테고리 코드 존재 여부 & 단위 존재 여부)
        Menu menu = menuRepository
                .findByUserIdAndMenuId(userId, menuId)
                .orElseThrow(() -> new BusinessException(CatalogErrorCode.NOTFOUND_MENU));

        IngredientCategory ingredientCategoryByCode = codeFinder.findIngredientCategoryByCode(request.ingredientCategoryCode());
        Unit unit = codeFinder.findUnitByCode(request.unitCode());

        StoreInfo storeInfo = userQueryApi.findStoreByUserId(userId);
        BigDecimal laborCost = calculator.calLaborCost(storeInfo.includeWeeklyHolidayPay(), storeInfo.laborCost());

        // 중복 조회 (해당 이름을 가진 재료가 존재하는지)
        String ingredientName = nameResolver.createNonDupIngredientName(userId, request.ingredientName());

        // 재료 등록

        // 단가 계산
        BigDecimal unitPrice = calculator.calUnitPrice(unit, request.price(), request.amount());

        // 단가 유효성 검증 0.00 이상
//        if(unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
//            throw new BusinessException(CatalogErrorCode.INVALID_UNIT_PRICE);
//        }

        Ingredient ingredient = ingredientRepository.save(
                Ingredient.create(
                        userId,
                        request.ingredientCategoryCode(),
                        ingredientName,
                        request.unitCode(),
                        unitPrice,
                        request.supplier()
                )
        );

        // 히스토리 등록
        IngredientPriceHistory history = ingredientPriceHistoryRepository.save(
                IngredientPriceHistory.create(
                        ingredient.getIngredientId(),
                        ingredient.getCurrentUnitPrice(),
                        ingredient.getUnitCode(),
                        request.amount(),
                        request.price(),
                        null
                )
        );

        // 레시피 등록
        Recipe recipe = recipeRepository.save(
                Recipe.create(
                        menuId,
                        ingredient.getIngredientId(),
                        request.amount()
                )
        );

        // analysis 재계산 + 업데이트
        // 총 원가 재계산
        List<Recipe> recipes = recipeRepository.findByMenuId(menuId);

        BigDecimal totalCost = calculator.calTotalCostWithRecipes(
                userId,
                recipes
        );

        MenuCostAnalysis analysis = calculator.calAnalysis(
                totalCost,
                menu.getSellingPrice(),
                laborCost,
                menu.getWorkTime()
        );

        menu.update(
                totalCost,
                analysis.costRate(),
                analysis.contributionMargin(),
                analysis.marginRate(),
                analysis.marginGradeCode(),
                analysis.recommendedPrice()
        );
    }
    /**
     * 메뉴명 수정
     */
    @Transactional(transactionManager = "transactionManager")
    public void updateMenuName(
            Long userId, Long menuId, String menuName
    ) {
        // 유효성 검사
        Menu menu = menuRepository
                .findByUserIdAndMenuId(userId, menuId)
                .orElseThrow(() -> new BusinessException(CatalogErrorCode.NOTFOUND_MENU));

        // 메뉴명 중복 해결 후 수정
        menu.updateName(nameResolver.createNonDupMenuName(userId, menuName));
    }

    /**
     * 메뉴 판매가 수정
     */
    @Transactional(transactionManager = "transactionManager")
    public void updateSellingPrice(
            Long userId,
            Long menuId,
            BigDecimal sellingPrice
    ) {
        // 유효성 검증 (메뉴 존재 여부)
        Menu menu = menuRepository
                .findByUserIdAndMenuId(userId, menuId)
                .orElseThrow(() -> new BusinessException(CatalogErrorCode.NOTFOUND_MENU));

        StoreInfo storeInfo = userQueryApi.findStoreByUserId(userId);
        BigDecimal laborCost = calculator.calLaborCost(storeInfo.includeWeeklyHolidayPay(), storeInfo.laborCost());

        // 메뉴 analysis 재계산
        MenuCostAnalysis analysis = calculator.calAnalysis(
                menu.getTotalCost(),
                sellingPrice,
                laborCost,
                menu.getWorkTime()
        );

        menu.updateSellingPrice(
                sellingPrice,
                analysis.costRate(),
                analysis.contributionMargin(),
                analysis.marginRate(),
                analysis.marginGradeCode(),
                analysis.recommendedPrice()
        );
    }

    /**
     * 카테고리 수정
     */
    @Transactional(transactionManager = "transactionManager")
    public void updateMenuCategory(
            Long userId, Long menuId, String category
    ) {
        // 유효성 검사
        Menu menu = menuRepository
                .findByUserIdAndMenuId(userId, menuId)
                .orElseThrow(() -> new BusinessException(CatalogErrorCode.NOTFOUND_MENU));

        if(!codeFinder.existsMenuCategory(category)) throw new BusinessException(CatalogErrorCode.NOTFOUND_CATEGORY);

        menu.updateCategory(category);
    }

    /**
     * 메뉴 제조시간 수정
     */
    @Transactional(transactionManager = "transactionManager")
    public void updateWorkTime(
            Long userId,
            Long menuId,
            Integer workTime
    ) {
        // 유효성 검증
        Menu menu = menuRepository
                .findByUserIdAndMenuId(userId, menuId)
                .orElseThrow(() -> new BusinessException(CatalogErrorCode.NOTFOUND_MENU));

        StoreInfo storeInfo = userQueryApi.findStoreByUserId(userId);
        BigDecimal laborCost = calculator.calLaborCost(storeInfo.includeWeeklyHolidayPay(), storeInfo.laborCost());

        // 메뉴 analysis 재계산
        MenuCostAnalysis analysis = calculator.calAnalysis(
                menu.getTotalCost(),
                menu.getSellingPrice(),
                laborCost,
                workTime
        );

        menu.updateWorkTime(
                workTime,
                analysis.costRate(),
                analysis.contributionMargin(),
                analysis.marginRate(),
                analysis.marginGradeCode(),
                analysis.recommendedPrice()
        );
    }

    /**
     * 레시피 수정 (only 사용량)
     */
    @Transactional(transactionManager = "transactionManager")
    public void updateRecipe(
            Long userId, Long menuId, Long recipeId, BigDecimal amount
    ) {
        // 레시피 존재 여부 확인
        Recipe recipe = recipeRepository
                .findByRecipeId(recipeId)
                .orElseThrow(() -> new BusinessException(CatalogErrorCode.NOTFOUND_RECIPE));

        if(recipe.getAmount().compareTo(amount) == 0) {
            return;
        }

        // 메뉴 존재 여부 확인
        Menu menu = menuRepository
                .findByUserIdAndMenuId(userId, menuId)
                .orElseThrow(() -> new BusinessException(CatalogErrorCode.NOTFOUND_MENU));

        StoreInfo storeInfo = userQueryApi.findStoreByUserId(userId);
        BigDecimal laborCost = calculator.calLaborCost(storeInfo.includeWeeklyHolidayPay(), storeInfo.laborCost());

        // 레시피 수정

        // 레시피 & 메뉴 analysis 수정 (레시피 amount가 바뀐 경우만)
        recipe.updateAmount(amount);


        List<Recipe> recipes = recipeRepository.findByMenuId(menuId);
        BigDecimal totalCost = calculator.calTotalCostWithRecipes(userId, recipes);

        MenuCostAnalysis analysis = calculator.calAnalysis(
                totalCost,
                menu.getSellingPrice(),
                laborCost,
                menu.getWorkTime()
        );

        menu.update(
                totalCost,
                analysis.costRate(),
                analysis.contributionMargin(),
                analysis.marginRate(),
                analysis.marginGradeCode(),
                analysis.recommendedPrice()
        );
    }

    /**
     * 레시피 삭제 (복수 선택 가능) -> 해당 메뉴 정보 업데이트 필요
     */
    @Transactional(transactionManager = "transactionManager")
    public void deleteRecipes(
            Long userId, Long menuId, DeleteRecipesRequest request
    ) {
        //메뉴 존재 여부 확인
        Menu menu = menuRepository
                .findByUserIdAndMenuId(userId, menuId)
                .orElseThrow(() -> new BusinessException(CatalogErrorCode.NOTFOUND_MENU));

        // 레시피 존재 여부 확인
        if(request.recipeIds().isEmpty()) {
            return;
        }

        StoreInfo storeInfo = userQueryApi.findStoreByUserId(userId);
        BigDecimal laborCost = calculator.calLaborCost(storeInfo.includeWeeklyHolidayPay(), storeInfo.laborCost());

        List<Recipe> targets = recipeRepository.findAllById(request.recipeIds());

        // 요청한 ID와 실제 조회된 개수 비교
        if(request.recipeIds().size() != targets.size()) {
            throw new BusinessException(CatalogErrorCode.NOTFOUND_RECIPE);
        }

        // 모든 레시피가 해당 메뉴에 속하는지 검증
        boolean allBelongToMenu = targets.stream()
                .allMatch(recipe -> recipe.getMenuId().equals(menuId));

        if (!allBelongToMenu) {
            throw new BusinessException(CatalogErrorCode.NOTFOUND_RECIPE);
        }

        // 레시피 삭제 (배치)
        recipeRepository.deleteAll(targets);

        // 메뉴 analysis 업데이트
        List<Recipe> nonTargets = recipeRepository.findByMenuId(menuId);
        BigDecimal totalCost = calculator.calTotalCostWithRecipes(userId, nonTargets);

        MenuCostAnalysis analysis = calculator.calAnalysis(
                totalCost,
                menu.getSellingPrice(),
                laborCost,
                menu.getWorkTime()
        );

        menu.update(
                totalCost,
                analysis.costRate(),
                analysis.contributionMargin(),
                analysis.marginRate(),
                analysis.marginGradeCode(),
                analysis.recommendedPrice()
        );
    }

    /**
     * 메뉴 삭제 (단일)
     */
    @Transactional(transactionManager = "transactionManager")
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

    /*---------홈화면-------*/
    public HomeMenusResponse getHomeMenus(Long userId) {
        List<Menu> menus = menuRepository.findByUserId(userId);

        // 위험 등급 메뉴만 분류
        List<Menu> dangerMenus = menus.stream()
                .filter(menu -> menu.getMarginGradeCode().equals("DANGER"))
                .toList();

        // 평균 원가율, 마진율
        BigDecimal avgCostRate = calculator.calAvgCostRate(menus);
        BigDecimal avgMarginRate = calculator.calAvgMarginRate(menus);

        String marginGrade = calculator.calMarginGrade(avgCostRate);

        return new HomeMenusResponse(
                dangerMenus.size(),
                new AvgCostRate(avgCostRate, marginGrade),
                avgMarginRate
        );
    }
}
