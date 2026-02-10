package com.coachcoach.insight.repository;

import com.coachcoach.insight.domain.StrategyBaselines;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StrategyBaseLinesRepository extends JpaRepository<StrategyBaselines, Long> {
    List<StrategyBaselines> findByUserIdAndStrategyDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    List<StrategyBaselines> findByUserId(Long userId);
}
