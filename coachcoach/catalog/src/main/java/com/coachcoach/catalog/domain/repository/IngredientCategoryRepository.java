package com.coachcoach.catalog.domain.repository;

import com.coachcoach.catalog.domain.entity.IngredientCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientCategoryRepository extends JpaRepository<IngredientCategory, Long> {
    List<IngredientCategory> findAllByOrderByDisplayOrderAsc();
}
