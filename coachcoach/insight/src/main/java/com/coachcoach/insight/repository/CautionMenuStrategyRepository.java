package com.coachcoach.insight.repository;

import com.coachcoach.insight.domain.CautionMenuStrategy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CautionMenuStrategyRepository extends JpaRepository<CautionMenuStrategy, Long> {
}
