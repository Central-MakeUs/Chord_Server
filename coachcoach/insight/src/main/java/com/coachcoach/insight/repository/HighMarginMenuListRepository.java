package com.coachcoach.insight.repository;

import com.coachcoach.insight.domain.HighMarginMenuList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HighMarginMenuListRepository extends JpaRepository<HighMarginMenuList, Long> {
    List<HighMarginMenuList> findByStrategyId(Long strategyId);
}
