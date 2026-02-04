package com.coachcoach.insight.repository;

import com.coachcoach.insight.domain.HighMarginMenuStrategy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HighMarginMenuStrategyRepository extends JpaRepository<HighMarginMenuStrategy, Long> {
}
