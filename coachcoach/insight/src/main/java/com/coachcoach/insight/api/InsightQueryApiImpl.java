package com.coachcoach.insight.api;

import com.coachcoach.common.api.InsightQueryApi;
import com.coachcoach.insight.repository.CautionMenuStrategyRepository;
import com.coachcoach.insight.repository.DangerMenuStrategyRepository;
import com.coachcoach.insight.repository.HighMarginMenuStrategyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InsightQueryApiImpl implements InsightQueryApi {

    private final DangerMenuStrategyRepository dangerMenuStrategyRepository;
    private final CautionMenuStrategyRepository cautionMenuStrategyRepository;
    private final HighMarginMenuStrategyRepository highMarginMenuStrategyRepository;

    @Override
    public void deleteByUserId(Long userId) {
        dangerMenuStrategyRepository.deleteByUserId(userId);
        cautionMenuStrategyRepository.deleteByUserId(userId);
        highMarginMenuStrategyRepository.deleteByUserId(userId);
    }
}
