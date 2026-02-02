package com.coachcoach.catalog.repository;

import com.coachcoach.catalog.domain.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findByIngredientId(Long ingredientId);
    boolean existsByMenuIdAndIngredientId(Long menuId, Long ingredientId);
    List<Recipe> findByMenuIdOrderByRecipeIdAsc(Long menuId);
    List<Recipe> findByMenuId(Long menuId);
    void deleteByMenuId(Long menuId);
    Optional<Recipe> findByRecipeId(Long recipeId);
    List<Recipe> findByRecipeIdIn(List<Long> menuIds);
}
