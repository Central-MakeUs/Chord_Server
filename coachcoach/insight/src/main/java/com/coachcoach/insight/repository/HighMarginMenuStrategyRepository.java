package com.coachcoach.insight.repository;

import com.coachcoach.insight.domain.HighMarginMenuStrategy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HighMarginMenuStrategyRepository extends JpaRepository<HighMarginMenuStrategy, Long> {
    Optional<HighMarginMenuStrategy> findByUserIdAndStrategyId(Long userId, Long strategyId);
    void deleteByUserId(Long userId);
}
