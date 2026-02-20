package com.coachcoach.insight.api;

import com.coachcoach.common.api.InsightQueryApi;
import com.coachcoach.insight.domain.DangerMenuStrategy;
import com.coachcoach.insight.domain.StrategyBaselines;
import com.coachcoach.insight.repository.CautionMenuStrategyRepository;
import com.coachcoach.insight.repository.DangerMenuStrategyRepository;
import com.coachcoach.insight.repository.HighMarginMenuStrategyRepository;
import com.coachcoach.insight.repository.StrategyBaseLinesRepository;
import com.coachcoach.insight.util.DateCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InsightQueryApiImpl implements InsightQueryApi {

    private final StrategyBaseLinesRepository strategyBaseLinesRepository;
    private final DangerMenuStrategyRepository dangerMenuStrategyRepository;
    private final CautionMenuStrategyRepository cautionMenuStrategyRepository;
    private final HighMarginMenuStrategyRepository highMarginMenuStrategyRepository;
    private final DateCalculator dateCalculator;

    @Override
    public void deleteByUserId(Long userId) {
        List<StrategyBaselines> baselines = strategyBaseLinesRepository.findByUserId(userId);
        List<Long> baselineIds = baselines.stream().map(StrategyBaselines::getBaselineId).toList();

        dangerMenuStrategyRepository.deleteByBaselineIdIn(baselineIds);
        cautionMenuStrategyRepository.deleteByBaselineIdIn(baselineIds);
        highMarginMenuStrategyRepository.deleteByBaselineIdIn(baselineIds);

    }

    @Override
    public Long getNumOfDangerMenus(Long userId) {

        LocalDate[] startDateAndEndDate = dateCalculator.getStartAndEndOfWeekByCurrentTime();
        LocalDate startDate = startDateAndEndDate[0];
        LocalDate endDate = startDateAndEndDate[1];

        List<StrategyBaselines> baselines = strategyBaseLinesRepository.findByUserIdAndStrategyDateBetween(userId, startDate, endDate);
        List<Long> baselineIds = baselines.stream().map(StrategyBaselines::getBaselineId).toList();
        return dangerMenuStrategyRepository.countByBaselineIdIn(baselineIds);
    }
}
