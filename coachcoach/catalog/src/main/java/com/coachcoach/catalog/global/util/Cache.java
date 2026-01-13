package com.coachcoach.catalog.global.util;

import com.coachcoach.catalog.domain.entity.IngredientCategory;
import com.coachcoach.catalog.domain.entity.MarginGrade;
import com.coachcoach.catalog.domain.entity.MenuCategory;
import com.coachcoach.catalog.domain.entity.Unit;
import com.coachcoach.catalog.domain.repository.IngredientCategoryRepository;
import com.coachcoach.catalog.domain.repository.MarginGradeRepository;
import com.coachcoach.catalog.domain.repository.MenuCategoryRepository;
import com.coachcoach.catalog.domain.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly=true)
public class Cache {
    private final IngredientCategoryRepository ingredientCategoryRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private final UnitRepository unitRepository;
    private final MarginGradeRepository marginGradeRepository;

    @Cacheable(value = "ingredient-categories", key = "'all'")
    public List<IngredientCategory> getIngredientCategories() {
        return ingredientCategoryRepository.findAllByOrderByDisplayOrderAsc();
    }

    @Cacheable(value = "menu-categories", key = "'all'")
    public List<MenuCategory> getMenuCategories() {
        return menuCategoryRepository.findAllByOrderByDisplayOrderAsc();
    }

    @Cacheable(value = "unit-code", key = "'all'")
    public List<Unit> getUnits() {
        return unitRepository.findAllByOrderByUnitIdAsc();
    }

    @Cacheable(value="margin-grade", key="'all'")
    public List<MarginGrade> getMarginGrades() { return marginGradeRepository.findAllByOrderByGradeIdAsc(); }
}
