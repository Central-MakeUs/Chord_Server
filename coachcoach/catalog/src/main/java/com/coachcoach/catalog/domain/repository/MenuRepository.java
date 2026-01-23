package com.coachcoach.catalog.domain.repository;

import com.coachcoach.catalog.domain.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

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

    Optional<Menu> findByUserIdAndMenuId(Long userId, Long menuId);
    boolean existsByUserIdAndMenuId(Long userId, Long menuId);
    boolean existsByUserIdAndMenuName(Long userId, String menuName);
    List<Menu> findByUserIdAndMenuCategoryCodeOrderByMenuIdDesc(Long userId, String categoryCode);
    List<Menu> findByUserIdOrderByMenuIdDesc(Long userId);
}
