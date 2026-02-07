package com.coachcoach.insight.repository;

import com.coachcoach.insight.domain.DangerMenuStrategy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DangerMenuStrategyRepository extends JpaRepository<DangerMenuStrategy, Long> {
    void deleteByUserId(Long userId);
}