package com.coachcoach.catalog.repository;

import com.coachcoach.catalog.domain.MarginGrade;
import com.coachcoach.catalog.domain.Menu;
import com.coachcoach.catalog.dto.MenuInUse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    @Query(
            value = "SELECT m.menu_name, r.amount " +
                    "FROM tb_recipe r " +
                    "JOIN tb_menu m ON r.menu_id = m.menu_id " +
                    "WHERE r.ingredient_id = :ingredientId " +
                    "AND m.user_id = :userId " +
                    "ORDER BY m.menu_id ASC",
            nativeQuery = true
    )
    List<MenuInUse> findMenusByUserIdAndIngredientId(Long userId, Long ingredientId);

    Optional<Menu> findByUserIdAndMenuId(Long userId, Long menuId);
    boolean existsByUserIdAndMenuId(Long userId, Long menuId);
    boolean existsByUserIdAndMenuName(Long userId, String menuName);
    List<Menu> findByUserIdAndMenuCategoryCodeOrderByMenuIdDesc(Long userId, String categoryCode);
    List<Menu> findByUserIdOrderByMenuIdDesc(Long userId);
    List<Menu> findByUserId(Long userId); // 회원 탈퇴용 (정렬 X)
    void deleteByUserId(Long userId);
    int countByUserIdAndMarginGradeCode(Long userId, String marginGradeCode);
    List<Menu> findByMenuIdIn(List<Long> menuIds);
}
