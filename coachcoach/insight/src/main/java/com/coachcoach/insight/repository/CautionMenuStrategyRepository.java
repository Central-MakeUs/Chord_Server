package com.coachcoach.insight.repository;

import com.coachcoach.insight.domain.CautionMenuStrategy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CautionMenuStrategyRepository extends JpaRepository<CautionMenuStrategy, Long> {
    Optional<CautionMenuStrategy> findByUserIdAndStrategyId(Long userId, Long strategyId);
    void deleteByUserId(Long userId);
}
