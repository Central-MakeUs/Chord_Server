package com.coachcoach.catalog.repository;

import com.coachcoach.catalog.entity.IngredientPriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredientPriceHistoryRepository extends JpaRepository<IngredientPriceHistory,Long> {
    Optional<IngredientPriceHistory> findFirstByIngredientIdOrderByHistoryIdDesc(Long ingredientId);
    List<IngredientPriceHistory> findByIngredientIdOrderByHistoryIdDesc(Long ingredientId);
}