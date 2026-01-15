package com.coachcoach.catalog.domain.repository;

import com.coachcoach.catalog.domain.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient,Long> {
    boolean existsByUserIdAndIngredientName(Long userId, String ingredientName);
    boolean existsByUserIdAndIngredientId(Long userId, Long ingredientId);
    List<Ingredient> findAllByUserIdOrderByIngredientIdDesc(Long userId);
    List<Ingredient> findByUserIdAndIngredientCategoryCodeInOrderByIngredientIdDesc(Long userId, List<String> categoryCodes);
    @Query(
            value = "SELECT * FROM tb_ingredient i WHERE i.user_id = :userId " +
                    "AND (i.ingredient_category_code IN :categoryCodes OR i.is_favorite = true) " +
                    "ORDER BY i.ingredient_id DESC",
            nativeQuery = true
    )
    List<Ingredient> findByUserIdAndCategoryCodesOrFavorite(
            @Param("userId") Long userId,
            @Param("categoryCodes") List<String> categoryCodes
    );
    Optional<Ingredient> findByUserIdAndIngredientId(Long userId, Long ingredientId);
    List<Ingredient> findByUserIdAndIngredientIdIn(Long userId, List<Long> ingredientIds);
    List<Ingredient> findByUserIdAndIngredientNameIn(Long userId, List<String> ingredientNames);
}
