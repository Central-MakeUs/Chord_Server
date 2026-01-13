package com.coachcoach.catalog.domain.repository;

import com.coachcoach.catalog.domain.entity.IngredientPriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredientPriceHistoryRepository extends JpaRepository<IngredientPriceHistory,Long> {
    Optional<IngredientPriceHistory> findFirstByIngredientIdOrderByHistoryIdDesc(Long ingredientId);
    List<IngredientPriceHistory> findByIngredientIdOrderByHistoryIdDesc(Long ingredientId);
}