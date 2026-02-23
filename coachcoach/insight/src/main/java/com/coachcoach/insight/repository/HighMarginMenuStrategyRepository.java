package com.coachcoach.insight.repository;

import com.coachcoach.insight.domain.CautionMenuStrategy;
import com.coachcoach.insight.domain.DangerMenuStrategy;
import com.coachcoach.insight.domain.HighMarginMenuStrategy;
import com.coachcoach.insight.domain.enums.StrategyState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HighMarginMenuStrategyRepository extends JpaRepository<HighMarginMenuStrategy, Long> {
    List<HighMarginMenuStrategy> findByBaselineIdIn(List<Long> baselineIds);
    List<HighMarginMenuStrategy> findByBaselineIdInAndStateIn(List<Long> baselineId, List<StrategyState> states);
    @Query(
            value = "SELECT h.* " +
                    "FROM tb_high_margin_menu_strategy h JOIN tb_strategy_baselines b ON h.baseline_id = b.baseline_id " +
                    "WHERE b.user_id = :userId AND h.strategy_id = :strategyId",
            nativeQuery = true
    )
    Optional<HighMarginMenuStrategy> findByUserIdAndStrategyId(
            @Param("userId") Long userId,
            @Param("strategyId") Long strategyId
    );
    void deleteByBaselineIdIn(List<Long> baselineIds);
}
