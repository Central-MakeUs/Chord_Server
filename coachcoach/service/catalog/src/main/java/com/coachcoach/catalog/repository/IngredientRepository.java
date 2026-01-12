package com.coachcoach.catalog.repository;

import com.coachcoach.catalog.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient,Long> {
    boolean existsByUserIdAndIngredientName(Long userId, String ingredientName);
    List<Ingredient> findAllByUserIdOrderByIngredientIdDesc(Long userId);
    List<Ingredient> findByUserIdAndIngredientCategoryCodeInOrderByIngredientIdDesc(Long userId, List<String> categoryCodes);
    Optional<Ingredient> findByUserIdAndIngredientId(Long userId, Long ingredientId);
}
