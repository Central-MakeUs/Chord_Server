package com.coachcoach.insight.service;

import com.coachcoach.common.dto.internal.MenuInfo;
import com.coachcoach.common.dto.internal.StoreInfo;
import com.coachcoach.common.exception.BusinessException;
import com.coachcoach.insight.domain.*;
import com.coachcoach.insight.domain.enums.CautionMenuCompletionPhraseTemplate;
import com.coachcoach.insight.domain.enums.DangerMenuCompletionPhraseTemplate;
import com.coachcoach.insight.domain.enums.StrategyState;
import com.coachcoach.insight.domain.enums.StrategyType;
import com.coachcoach.insight.exception.InsightErrorCode;
import com.coachcoach.insight.repository.CautionMenuStrategyRepository;
import com.coachcoach.insight.repository.DangerMenuStrategyRepository;
import com.coachcoach.insight.repository.HighMarginMenuStrategyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.DefaultBeanNameGenerator;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StrategyService {

    private final DangerMenuStrategyRepository dangerMenuStrategyRepository;
    private final CautionMenuStrategyRepository cautionMenuStrategyRepository;
    private final HighMarginMenuStrategyRepository highMarginMenuStrategyRepository;

    public List<Strategy> findBySavedTrueAndBaselineIdInAndStateIn(List<Long> baselineId, List<StrategyState> states) {
        List<DangerMenuStrategy> dangerMenuStrategies = dangerMenuStrategyRepository.findByBaselineIdInAndStateIn(baselineId, states);
        List<CautionMenuStrategy> cautionMenuStrategies = cautionMenuStrategyRepository.findByBaselineIdInAndStateIn(baselineId, states);
        List<HighMarginMenuStrategy> highMarginMenuStrategies = highMarginMenuStrategyRepository.findByBaselineIdInAndStateIn(baselineId, states);

        List<Strategy> all = new ArrayList<>(dangerMenuStrategies.size() + cautionMenuStrategies.size() + highMarginMenuStrategies.size());

        all.addAll(dangerMenuStrategies);
        all.addAll(cautionMenuStrategies);
        all.addAll(highMarginMenuStrategies);

        return all;
    }
    public List<Strategy> findByBaselineIdIn(List<Long> baselineIds) {
        // 위험 전략 조회
        List<DangerMenuStrategy> dangerMenuStrategies = dangerMenuStrategyRepository.findByBaselineIdIn(baselineIds);

        // 주의 전략 조회
        List<CautionMenuStrategy> cautionMenuStrategies = cautionMenuStrategyRepository.findByBaselineIdIn(baselineIds);

        // 고마진 전략 조회
        List<HighMarginMenuStrategy> highMarginMenuStrategies = highMarginMenuStrategyRepository.findByBaselineIdIn(baselineIds);

        List<Strategy> strategies = new ArrayList<>(dangerMenuStrategies.size() + cautionMenuStrategies.size() + highMarginMenuStrategies.size());
        strategies.addAll(dangerMenuStrategies);
        strategies.addAll(cautionMenuStrategies);
        strategies.addAll(highMarginMenuStrategies);

        return  strategies;
    }

    public Strategy findByUserIdAndStrategyId(Long userId, Long strategyId, StrategyType type) {
        return switch (type) {
            case DANGER -> dangerMenuStrategyRepository.findByUserIdAndStrategyId(userId, strategyId)
                    .orElseThrow(() -> new BusinessException(InsightErrorCode.NOTFOUND_STRATEGY));
            case CAUTION -> cautionMenuStrategyRepository.findByUserIdAndStrategyId(userId, strategyId)
                    .orElseThrow(() -> new BusinessException(InsightErrorCode.NOTFOUND_STRATEGY));
            case HIGH_MARGIN -> highMarginMenuStrategyRepository.findByUserIdAndStrategyId(userId, strategyId)
                    .orElseThrow(() -> new BusinessException(InsightErrorCode.NOTFOUND_STRATEGY));
            default -> throw new BusinessException(InsightErrorCode.NOTFOUND_STRATEGY_TYPE);
        };
    }

    public String getCompletionPhrase(Strategy strategy, MenuSnapshots menuInfo, StoreInfo storeInfo, BigDecimal marginRateImprovement) {
        return switch (strategy.getType()) {
            case DANGER -> {
                if(marginRateImprovement.compareTo(BigDecimal.ZERO) < 0) {
                    yield MessageFormat.format(DangerMenuCompletionPhraseTemplate.NEGATIVE.getCompletionPhrase(),
                            marginRateImprovement.abs(),
                            menuInfo.getMenuName(),
                            storeInfo.name()
                    );
                } else if(strategy.getGuideCode().equals("REMOVE_MENU")) {
                    yield MessageFormat.format(DangerMenuCompletionPhraseTemplate.REMOVE_MENU.getCompletionPhrase(),
                        menuInfo.getMenuName(),
                                storeInfo.name(),
                                marginRateImprovement
                    );
                } else if(strategy.getGuideCode().equals("ADJUST_PRICE")) {
                    yield MessageFormat.format(DangerMenuCompletionPhraseTemplate.ADJUST_PRICE.getCompletionPhrase(),
                            storeInfo.name(),
                            marginRateImprovement
                    );
                } else {
                    throw new BusinessException(InsightErrorCode.NOTFOUND_GUIDE_CODE);
                }
            }
            case CAUTION -> {
                if(marginRateImprovement.compareTo(BigDecimal.ZERO) < 0) {
                    log.info(marginRateImprovement.toString());
                    log.info("0");
                    yield MessageFormat.format(CautionMenuCompletionPhraseTemplate.NEGATIVE.getCompletionPhrase(),
                            marginRateImprovement.abs(),
                            menuInfo.getMenuName(),
                            storeInfo.name()
                    );
                } else if(strategy.getGuideCode().equals("ADJUST_PRICE")) {
                    log.info("1");
                    yield MessageFormat.format(CautionMenuCompletionPhraseTemplate.ADJUST_PRICE.getCompletionPhrase(),
                            storeInfo.name(),
                            marginRateImprovement
                    );
                } else if(strategy.getGuideCode().equals("ADJUST_RECIPE")) {
                    yield MessageFormat.format(CautionMenuCompletionPhraseTemplate.ADJUST_RECIPE.getCompletionPhrase(),
                            storeInfo.name(),
                            marginRateImprovement
                    );
                } else {
                    throw new BusinessException(InsightErrorCode.NOTFOUND_GUIDE_CODE);
                }
            }
            case HIGH_MARGIN -> strategy.getCompletionPhrase();
            default -> throw new BusinessException(InsightErrorCode.NOTFOUND_STRATEGY_TYPE);
        };
    }

    /**
     * for Danger / Caution
     */
    public List<Strategy> findByMenuId(Long menuId) {
        List<DangerMenuStrategy> dangerMenuStrategies = dangerMenuStrategyRepository.findByMenuId(menuId);
        List<CautionMenuStrategy> cautionMenuStrategies = cautionMenuStrategyRepository.findByMenuId(menuId);

        List<Strategy> all = new ArrayList<>(dangerMenuStrategies.size() + cautionMenuStrategies.size());
        all.addAll(dangerMenuStrategies);
        all.addAll(cautionMenuStrategies);

        return all;
    }
}
