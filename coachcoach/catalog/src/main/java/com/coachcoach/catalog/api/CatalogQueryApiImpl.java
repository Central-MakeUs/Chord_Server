package com.coachcoach.catalog.api;

import com.coachcoach.catalog.domain.Ingredient;
import com.coachcoach.catalog.domain.Menu;
import com.coachcoach.catalog.repository.IngredientPriceHistoryRepository;
import com.coachcoach.catalog.repository.IngredientRepository;
import com.coachcoach.catalog.repository.MenuRepository;
import com.coachcoach.catalog.repository.RecipeRepository;
import com.coachcoach.common.api.CatalogQueryApi;
import com.coachcoach.common.dto.internal.MenuInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogQueryApiImpl implements CatalogQueryApi {

    private final RecipeRepository recipeRepository;
    private final MenuRepository menuRepository;
    private final IngredientRepository ingredientRepository;
    private final IngredientPriceHistoryRepository ingredientPriceHistoryRepository;

    @Override
    public int countByUserIdAndMarginGradeCode(Long userId, String marginGradeCode) {
        return menuRepository.countByUserIdAndMarginGradeCode(userId, marginGradeCode);
    }

    public void deleteByUserId(Long userId) {
        // recipes
        List<Menu> deleteMenus = menuRepository.findByUserId(userId);
        List<Long> deleteMenuIds = deleteMenus.stream()
                .map(Menu::getMenuId)
                .toList();

        recipeRepository.deleteByMenuIdIn(deleteMenuIds);

        // menus
        menuRepository.deleteByUserId(userId);

        // ingredients & histories
        List<Ingredient> deleteIngredients = ingredientRepository.findByUserId(userId);
        List<Long> deleteIngredientIds = deleteIngredients.stream()
                .map(Ingredient::getIngredientId)
                .toList();

        ingredientPriceHistoryRepository.deleteByIngredientIdIn(deleteIngredientIds);
        ingredientRepository.deleteByUserId(userId);

    }

    @Override
    public List<MenuInfo> findByMenuIdIn(List<Long> menuIds) {
        List<Menu> menus = menuRepository.findByMenuIdIn(menuIds);

        return menus.stream()
                .map(menu -> {
                    return MenuInfo.builder()
                            .menuId(menu.getMenuId())
                            .userId(menu.getUserId())
                            .menuCategoryCode(menu.getMenuCategoryCode())
                            .menuName(menu.getMenuName())
                            .sellingPrice(menu.getSellingPrice())
                            .totalCost(menu.getTotalCost())
                            .costRate(menu.getCostRate())
                            .contributionMargin(menu.getContributionMargin())
                            .marginRate(menu.getMarginRate())
                            .marginGradeCode(menu.getMarginGradeCode())
                            .workTime(menu.getWorkTime())
                            .recommendedPrice(menu.getRecommendedPrice())
                            .createdAt(menu.getCreatedAt())
                            .updatedAt(menu.getUpdatedAt())
                            .build();
                })
                .toList();
    }
}
