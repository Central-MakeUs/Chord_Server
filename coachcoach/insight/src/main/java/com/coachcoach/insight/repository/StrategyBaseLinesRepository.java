package com.coachcoach.insight.repository;

import com.coachcoach.insight.domain.StrategyBaselines;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StrategyBaseLinesRepository extends JpaRepository<StrategyBaselines, Long> {
}
