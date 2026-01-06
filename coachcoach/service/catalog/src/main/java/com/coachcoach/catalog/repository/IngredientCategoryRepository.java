package com.coachcoach.catalog.repository;

import com.coachcoach.catalog.entity.IngredientCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientCategoryRepository extends JpaRepository<IngredientCategory, Long> {
    boolean existsByUserIdAndCategoryName(Long userId, String categoryName);
    List<IngredientCategory> findByUserIdOrderByCreatedAtAsc(Long userId);
}
