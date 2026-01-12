package com.coachcoach.catalog.repository;

import com.coachcoach.catalog.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    @Query(
            value = "SELECT m.menu_name " +
                    "FROM tb_recipe r " +
                    "JOIN tb_menu m ON r.menu_id = m.menu_id " +
                    "WHERE r.ingredient_id = :ingredientId " +
                    "AND m.user_id = :userId " +
                    "ORDER BY m.menu_id ASC",
            nativeQuery = true
    )
    List<String> findMenusByUserIdAndIngredientId(Long userId, Long ingredientId);
}
