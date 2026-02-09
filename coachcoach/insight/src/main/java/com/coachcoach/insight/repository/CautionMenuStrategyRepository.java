package com.coachcoach.insight.repository;

import com.coachcoach.insight.domain.CautionMenuStrategy;
import com.coachcoach.insight.domain.DangerMenuStrategy;
import com.coachcoach.insight.domain.enums.StrategyState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface CautionMenuStrategyRepository extends JpaRepository<CautionMenuStrategy, Long> {
    List<CautionMenuStrategy> findByBaselineIdIn(List<Long> baselineIds);
    List<CautionMenuStrategy> findBySavedTrueAndBaselineIdInAndStateIn(List<Long> baselineId, List<StrategyState> states);
    @Query(
            value = "SELECT c.* " +
                    "FROM tb_caution_menu_strategy c JOIN tb_strategy_baselines b ON c.baseline_id = b.baseline_id " +
                    "WHERE b.user_id = :userId AND c.strategy_id = :strategyId",
            nativeQuery = true
    )
    Optional<CautionMenuStrategy> findByUserIdAndStrategyId(
            @Param("userId") Long userId,
            @Param("strategyId") Long strategyId
    );
    void deleteByBaselineIdIn(List<Long> baselineIds);
}
