package com.coachcoach.catalog.repository;

import com.coachcoach.catalog.domain.Ingredient;
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
    @Query(
            value = "SELECT * " +
                    "FROM tb_ingredient i " +
                    "WHERE i.user_id = :userId AND i.ingredient_name LIKE CONCAT('%', :keyword, '%') " +
                    "ORDER BY i.ingredient_name ASC",
            nativeQuery = true
    )
    List<Ingredient> findByUserIdAndKeywordOrderByIngredientNameAsc(
            @Param("userId") Long userId,
            @Param("keyword") String keyword
    );

    @Query(
            value = "SELECT DISTINCT i.* " +  // DISTINCT 추가!
                    "FROM tb_ingredient i " +
                    "LEFT JOIN tb_recipe r ON i.ingredient_id = r.ingredient_id " +
                    "LEFT JOIN tb_menu m ON r.menu_id = m.menu_id " +  // r.menu_id 맞죠?
                    "WHERE i.user_id = :userId " +
                    "AND (i.ingredient_name LIKE CONCAT('%', :keyword, '%') " +
                    "       OR m.menu_name LIKE CONCAT('%', :keyword, '%')) " +
                    "ORDER BY i.ingredient_name ASC",
            nativeQuery = true
    )
    List<Ingredient> findByUserIdAndMenuNameAndIngredientNameOrderByIngredientNameAsc(
            @Param("userId") Long userId,
            @Param("keyword") String keyword
    );
    void deleteByUserId(Long userId);
    List<Ingredient> findByUserId(Long userId);
}
