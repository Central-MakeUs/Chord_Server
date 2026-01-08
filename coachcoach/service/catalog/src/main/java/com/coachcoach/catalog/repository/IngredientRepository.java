package com.coachcoach.catalog.repository;

import com.coachcoach.catalog.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient,Long> {
    boolean existsByUserIdAndIngredientName(Long userId, String ingredientName);
}
