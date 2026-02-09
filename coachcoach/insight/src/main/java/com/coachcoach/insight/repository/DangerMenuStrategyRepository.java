package com.coachcoach.insight.repository;

import com.coachcoach.insight.domain.DangerMenuStrategy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DangerMenuStrategyRepository extends JpaRepository<DangerMenuStrategy, Long> {
    List<DangerMenuStrategy> findByUserIdAndStrategyDateBetweenOrderByStrategyId(Long userId, LocalDate startDate, LocalDate endDate);
    Optional<DangerMenuStrategy> findByUserIdAndStrategyId(Long userId, Long strategyId);
    void deleteByUserId(Long userId);
}