package com.coachcoach.catalog.repository;

import com.coachcoach.catalog.entity.IngredientPriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientPriceHistoryRepository extends JpaRepository<IngredientPriceHistory,Long> {
}
