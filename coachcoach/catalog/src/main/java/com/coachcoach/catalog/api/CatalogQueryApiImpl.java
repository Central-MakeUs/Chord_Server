package com.coachcoach.catalog.api;

import com.coachcoach.catalog.domain.Ingredient;
import com.coachcoach.catalog.domain.Menu;
import com.coachcoach.catalog.repository.IngredientPriceHistoryRepository;
import com.coachcoach.catalog.repository.IngredientRepository;
import com.coachcoach.catalog.repository.MenuRepository;
import com.coachcoach.catalog.repository.RecipeRepository;
import com.coachcoach.common.api.CatalogQueryApi;
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
}
