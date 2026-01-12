package com.coachcoach.catalog.service;

import com.coachcoach.catalog.entity.IngredientCategory;
import com.coachcoach.catalog.entity.MenuCategory;
import com.coachcoach.catalog.repository.IngredientCategoryRepository;
import com.coachcoach.catalog.repository.MenuCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly=true)
public class CacheService {
    private final IngredientCategoryRepository ingredientCategoryRepository;
    private final MenuCategoryRepository menuCategoryRepository;

    @Cacheable(value = "ingredient-categories", key = "'all'")
    public List<IngredientCategory> getIngredientCategories() {
        return ingredientCategoryRepository.findAllByOrderByDisplayOrderAsc();
    }

    @Cacheable(value = "menu-categories", key = "'all'")
    public List<MenuCategory> getMenuCategories() {
        return menuCategoryRepository.findAllByOrderByDisplayOrderAsc();
    }
}
