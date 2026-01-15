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
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

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
     * todo: 유사도 기반 나열
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
     * todo: 레시피 아이디는 반환에서 제외
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
                .map(x -> MenuResponse.of(x, codeFinder.getMarginNameByCode(x.getMarginGradeCode())))
                .toList();
    }

    /**
     * 메뉴 상세 정보 반환
     * todo: 변수명 통일 marginCode -> marginGradeCode/Name
     */
    public MenuDetailResponse readMenu(
            Long userId, Long menuId
    ) {
        Menu menu = menuRepository.findByUserIdAndMenuId(userId, menuId).orElseThrow(() -> new BusinessException(CatalogErrorCode.NOTFOUND_MENU));
        MarginGrade margin = codeFinder.findMarginCodeByCode(menu.getMarginGradeCode());
        return MenuDetailResponse.of(menu, margin.getGradeName(), margin.getMessage());
    }
}
