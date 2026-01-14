package com.coachcoach.catalog.domain.repository;

import com.coachcoach.catalog.domain.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findByIngredientId(Long ingredientId);
    boolean existsByMenuIdAndIngredientId(Long menuId, Long ingredientId);
    List<Recipe> findByMenuIdOrderByIngredientIdAsc(Long menuId);
}
