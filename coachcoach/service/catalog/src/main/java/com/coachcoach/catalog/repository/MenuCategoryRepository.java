package com.coachcoach.catalog.repository;

import com.coachcoach.catalog.entity.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuCategoryRepository extends JpaRepository<MenuCategory, Long> {
    boolean existsByUserIdAndCategoryName(Long userId, String categoryName);
    List<MenuCategory> findByUserIdOrderByCreatedAtAsc(Long userId);
}
