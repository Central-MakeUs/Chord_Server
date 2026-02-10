package com.coachcoach.insight.api;

import com.coachcoach.common.api.InsightQueryApi;
import com.coachcoach.insight.domain.StrategyBaselines;
import com.coachcoach.insight.repository.CautionMenuStrategyRepository;
import com.coachcoach.insight.repository.DangerMenuStrategyRepository;
import com.coachcoach.insight.repository.HighMarginMenuStrategyRepository;
import com.coachcoach.insight.repository.StrategyBaseLinesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InsightQueryApiImpl implements InsightQueryApi {

    private final StrategyBaseLinesRepository strategyBaseLinesRepository;
    private final DangerMenuStrategyRepository dangerMenuStrategyRepository;
    private final CautionMenuStrategyRepository cautionMenuStrategyRepository;
    private final HighMarginMenuStrategyRepository highMarginMenuStrategyRepository;

    @Override
    public void deleteByUserId(Long userId) {
        List<StrategyBaselines> baselines = strategyBaseLinesRepository.findByUserId(userId);
        List<Long> baselineIds = baselines.stream().map(StrategyBaselines::getBaselineId).toList();

        dangerMenuStrategyRepository.deleteByBaselineIdIn(baselineIds);
        cautionMenuStrategyRepository.deleteByBaselineIdIn(baselineIds);
        highMarginMenuStrategyRepository.deleteByBaselineIdIn(baselineIds);

    }
}
