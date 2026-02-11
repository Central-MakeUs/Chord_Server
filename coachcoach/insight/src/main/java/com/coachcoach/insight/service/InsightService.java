package com.coachcoach.insight.service;

import com.coachcoach.common.api.CatalogQueryApi;
import com.coachcoach.common.api.UserQueryApi;
import com.coachcoach.common.dto.internal.MenuInfo;
import com.coachcoach.common.dto.internal.StoreInfo;
import com.coachcoach.common.exception.BusinessException;
import com.coachcoach.insight.domain.*;
import com.coachcoach.insight.domain.enums.CautionMenuCompletionPhraseTemplate;
import com.coachcoach.insight.domain.enums.DangerMenuCompletionPhraseTemplate;
import com.coachcoach.insight.domain.enums.StrategyState;
import com.coachcoach.insight.domain.enums.StrategyType;
import com.coachcoach.insight.dto.response.*;
import com.coachcoach.insight.exception.InsightErrorCode;
import com.coachcoach.insight.repository.CautionMenuStrategyRepository;
import com.coachcoach.insight.repository.DangerMenuStrategyRepository;
import com.coachcoach.insight.repository.HighMarginMenuStrategyRepository;
import com.coachcoach.insight.repository.StrategyBaseLinesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InsightService {

    private final DangerMenuStrategyRepository dangerMenuStrategyRepository;
    private final CautionMenuStrategyRepository cautionMenuStrategyRepository;
    private final HighMarginMenuStrategyRepository highMarginMenuStrategyRepository;
    private final StrategyBaseLinesRepository strategyBaseLinesRepository;
    private final StrategyService strategyService;
    private final CatalogQueryApi catalogQueryApi;
    private final UserQueryApi userQueryApi;

    /*----AI 코치 탭----*/
    /**
     * 이번주 추천 전략
     * 정렬 순서: 진행중 -> 진행 전 -> 진행 완료
     * 진행 중 세부 순서: 진행 시작 날짜(내림차순)
     * 진행 전 세부 순서: 생성 순 (오름차순)
     * 진행 완료: 진행 완료 날짜(내림차순)
     */
    public List<StrategyBriefResponse> getWeeklyRecommendedStrategies(
            Long userId,
            int year,
            int month,
            int weekOfMonth
    ) {
        // 해당 주 시작일, 종료일
        LocalDate[] startAndEndOfWeek = getStartAndEndOfWeek(year, month, weekOfMonth);
        LocalDate startDate = startAndEndOfWeek[0];
        LocalDate endDate = startAndEndOfWeek[1];

        // 해당 주 전략 baseline id
        List<StrategyBaselines> strategyBaseLine = strategyBaseLinesRepository.findByUserIdAndStrategyDateBetween(userId, startDate, endDate);
        List<Long> baseLineIds = strategyBaseLine.stream().map(StrategyBaselines::getBaselineId).toList();

        // 전략 리스트
        List<DangerMenuStrategy> dangerMenuStrategies = dangerMenuStrategyRepository.findByBaselineIdIn(baseLineIds);
        List<CautionMenuStrategy> cautionMenuStrategies = cautionMenuStrategyRepository.findByBaselineIdIn(baseLineIds);
        List<HighMarginMenuStrategy> highMarginMenuStrategies = highMarginMenuStrategyRepository.findByBaselineIdIn(baseLineIds);

        // 모든 전략
        List<StrategyBriefResponse> allStrategies = new ArrayList<>();

        dangerMenuStrategies.forEach(s -> allStrategies.add(
                convertToStrategyBrief(s, StrategyType.DANGER)
        ));
        cautionMenuStrategies.forEach(s -> allStrategies.add(
                convertToStrategyBrief(s, StrategyType.CAUTION)
        ));
        highMarginMenuStrategies.forEach(s -> allStrategies.add(
                convertToStrategyBrief(s, StrategyType.HIGH_MARGIN)
        ));

        Map<StrategyState, List<StrategyBriefResponse>> grouped = allStrategies.stream()
                .collect(Collectors.groupingBy(StrategyBriefResponse::state));

        List<StrategyBriefResponse> result = new ArrayList<>();

        // 1. 진행중 (시작 날짜 내림차순)
        result.addAll(
                grouped.getOrDefault(StrategyState.ONGOING, new ArrayList<>())
                        .stream()
                        .sorted(Comparator.comparing(StrategyBriefResponse::startDate).reversed())
                        .toList()
        );

        // 2. 진행 전 (생성 순)
        result.addAll(
                grouped.getOrDefault(StrategyState.BEFORE, new ArrayList<>())
                        .stream()
                        .sorted(Comparator.comparing(StrategyBriefResponse::createdAt))
                        .toList()
        );

        // 3. 완료 (완료 날짜 내림차순)
        result.addAll(
                grouped.getOrDefault(StrategyState.COMPLETED, new ArrayList<>())
                        .stream()
                        .sorted(Comparator.comparing(StrategyBriefResponse::completionDate).reversed())
                        .toList()
        );

        return result;
    }

    /**
     * 내가 저장한 전략 모음
     * 필터링 기준: (년+월) + 실행 완료/미완료
     * 실행 완료 기준: 실행 중 + 실행 완료
     * 정렬: 생성 날짜 내림차순
     */
    public List<SavedStrategyResponse> getSavedStrategies(Long userId, Integer year, Integer month, Boolean isCompleted) {
        LocalDate[] startAndEndOfMonth = getStartAndEndOfMonth(year, month);
        LocalDate startDate = startAndEndOfMonth[0];
        LocalDate endDate = startAndEndOfMonth[1];

        List<StrategyBaselines> strategyBaseLine = strategyBaseLinesRepository.findByUserIdAndStrategyDateBetween(userId, startDate, endDate);
        List<Long> baseLineIds = strategyBaseLine.stream().map(StrategyBaselines::getBaselineId).toList();

        List<StrategyState> states = (isCompleted) ? List.of(StrategyState.ONGOING, StrategyState.COMPLETED) : List.of(StrategyState.BEFORE);

        List<DangerMenuStrategy> dangerMenuStrategies = dangerMenuStrategyRepository.findBySavedTrueAndBaselineIdInAndStateIn(baseLineIds, states);
        List<CautionMenuStrategy> cautionMenuStrategies = cautionMenuStrategyRepository.findBySavedTrueAndBaselineIdInAndStateIn(baseLineIds, states);
        List<HighMarginMenuStrategy> highMarginMenuStrategies = highMarginMenuStrategyRepository.findBySavedTrueAndBaselineIdInAndStateIn(baseLineIds, states);

        List<SavedStrategyResponse> allStrategies = new ArrayList<>();

        allStrategies.addAll(
                dangerMenuStrategies.stream()
                        .map(strategy -> new SavedStrategyResponse(
                                strategy.getStrategyId(),
                                strategy.getState(),
                                StrategyType.DANGER,
                                strategy.getSummary(),
                                strategy.getDetail(),
                                getYear(strategy.getCreatedAt()),
                                getMonth(strategy.getCreatedAt()),
                                getWeekOfMonth(strategy.getCreatedAt()),
                                strategy.getCreatedAt()
                        ))
                        .toList()
        );
        allStrategies.addAll(
                cautionMenuStrategies.stream()
                        .map(strategy -> new SavedStrategyResponse(
                                strategy.getStrategyId(),
                                strategy.getState(),
                                StrategyType.CAUTION,
                                strategy.getSummary(),
                                strategy.getDetail(),
                                getYear(strategy.getCreatedAt()),
                                getMonth(strategy.getCreatedAt()),
                                getWeekOfMonth(strategy.getCreatedAt()),
                                strategy.getCreatedAt()
                        ))
                        .toList()
        );
        allStrategies.addAll(
                highMarginMenuStrategies.stream()
                        .map(strategy -> new SavedStrategyResponse(
                                strategy.getStrategyId(),
                                strategy.getState(),
                                StrategyType.HIGH_MARGIN,
                                strategy.getSummary(),
                                strategy.getDetail(),
                                getYear(strategy.getCreatedAt()),
                                getMonth(strategy.getCreatedAt()),
                                getWeekOfMonth(strategy.getCreatedAt()),
                                strategy.getCreatedAt()
                        ))
                        .toList()
        );

        return allStrategies.stream()
                .sorted(Comparator.comparing(SavedStrategyResponse::createdAt).reversed())
                .toList();
    }

    /**
     * 위험 메뉴 전략 상세
     */
//    public DangerMenuStrategyDetailResponse getDangerMenuStrategyDetail(Long strategyId, Long userId)

    /**
     * 주의 메뉴 전략 상세
     */
//    public CautionMenuStrategyDetailResponse getCautionMenuStrategyDetail(Long strategyId, Long userId)

    /**
     * 고마진 메뉴 추천 전략 상세
     */
//    public HighMarginMenuStrategyDetailResponse getHighMarginMenuStrategyDetail(Long strategyId, Long userId)

    /**
     * 전략 저장/해제
     */
    @Transactional(transactionManager = "transactionManager")
    public void toggleStrategySaved(Long strategyId, StrategyType type, Long userId, Boolean save) {
        Strategy strategy = strategyService.findByUserIdAndStrategyId(userId, strategyId, type);
        strategy.updateSaved(save);
    }

    /**
     * 전략 시작
     */
    @Transactional(transactionManager = "transactionManager")
    public void changeStateToOngoing(Long userId, Long strategyId, StrategyType strategyType) {
        Strategy strategy = strategyService.findByUserIdAndStrategyId(userId, strategyId, strategyType);
        checkStartCondition(strategy.getState());
        strategy.updateStateToOngoing();
    }

    /**
     * 전략 완료
     * 조건: state == "ongoing"
     */
    @Transactional(transactionManager = "transactionManager")
    public CompletionPhraseResponse changeStateToCompleted(Long userId, Long strategyId, StrategyType strategyType) {
        StoreInfo storeInfo = userQueryApi.findStoreByUserId(userId);
        BigDecimal avgMarginRate = catalogQueryApi.getAvgMarginRate(userId);

        // 전략 조회
        Strategy strategy = strategyService.findByUserIdAndStrategyId(userId, strategyId, strategyType);

        // 전략 Baseline 조회
        StrategyBaselines baseline = strategyBaseLinesRepository.findById(strategy.getBaselineId())
                .orElseThrow(() -> new BusinessException(InsightErrorCode.NOTFOUND_STRATEGY_BASELINE));

        // 전략에 해당하는 메뉴 조회
        MenuInfo menuInfo = (strategy.getType().equals(StrategyType.HIGH_MARGIN)) ? null : catalogQueryApi.findByUserIdAndMenuId(userId, strategy.getMenuId());

        // 완료로 업데이트
        checkCompletionCondition(strategy.getState());
        strategy.updateStateToCompleted();

        // 개선된 평균 마진률 계산
        BigDecimal marginRateImprovement = baseline.getAvgMarginRate().subtract(avgMarginRate);

        return new CompletionPhraseResponse(strategyService.getCompletionPhrase(strategy, menuInfo, storeInfo, marginRateImprovement));
    }

    /*---- 홈화면 ----*/
    public HomeStrategiesResponse getHomeStrategies(Long userId, int year, int month, int weekOfMonth) {
        LocalDate[] startAndEndOfWeek = getStartAndEndOfWeek(year, month, weekOfMonth);
        LocalDate startDate = startAndEndOfWeek[0];
        LocalDate endDate = startAndEndOfWeek[1];

        List<StrategyBaselines> baselines = strategyBaseLinesRepository.findByUserIdAndStrategyDateBetween(userId, startDate, endDate);
        List<Long> baselineIds = baselines.stream().map(StrategyBaselines::getBaselineId).toList();
        List<DangerMenuStrategy> dangerMenuStrategies = dangerMenuStrategyRepository.findByBaselineIdIn(baselineIds);
        List<Long> menuIds = dangerMenuStrategies.stream().map(DangerMenuStrategy::getMenuId).toList();
        List<MenuInfo> menus = catalogQueryApi.findByMenuIdIn(menuIds);

        Map<Long, MenuInfo> menuMap = menus.stream()
                .collect(Collectors.toMap(MenuInfo::menuId, menu -> menu));

        List<HomeStrategyBrief> results = dangerMenuStrategies.stream()
                .map(strategy -> {
                    MenuInfo m = menuMap.get(strategy.getMenuId());
                    return new HomeStrategyBrief(
                            m.menuId(),
                            m.menuName(),
                            strategy.getStrategyId(),
                            strategy.getState(),
                            StrategyType.DANGER,
                            strategy.getSummary(),
                            strategy.getCreatedAt()
                    );
                })
                .toList();

        return new HomeStrategiesResponse(results);
    }
    /*-----------------------*/

    /**
     * N월 N주차에 해당하는 시작일(월)과 끝일(일) 반환
     * @param year
     * @param month
     * @param weekOfMonth
     */
    private LocalDate[] getStartAndEndOfWeek(int year, int month, int weekOfMonth) {
        // 한국 기준 주차 설정 (월요일 시작)
        WeekFields weekFields = WeekFields.of(Locale.KOREA);

        // 해당 월의 1일 날짜 설정
        LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);

        // 해당 월의 N주차에 속하는 아무 날짜 추출
        LocalDate dateInWeek = firstDayOfMonth
                .with(weekFields.weekOfMonth(), weekOfMonth);

        // 해당 주의 월요일
        LocalDate startOfWeek = dateInWeek.with(weekFields.dayOfWeek(), 1);

        // 해당 주의 일요일
        LocalDate endOfWeek = dateInWeek.with(weekFields.dayOfWeek(), 7);

        return new LocalDate[]{startOfWeek, endOfWeek};
    }

    /**
     * 특정 년월의 시작일과 끝일 반환
     * @param year 년도
     * @param month 월
     * @return [시작일, 끝일]
     */
    private LocalDate[] getStartAndEndOfMonth(int year, int month) {
        LocalDate now = LocalDate.now();

        // 해당 월의 1일
        LocalDate startDate = LocalDate.of(year, month, 1);

        // 해당 월의 마지막 날
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        return new LocalDate[]{startDate, endDate};
    }

    /**
     * 년도 추출
     */
    private int getYear(LocalDateTime createdAt) {
        return createdAt.getYear();
    }

    /**
     * 월 추출
     */
    private int getMonth(LocalDateTime createdAt) {
        return createdAt.getMonthValue();
    }

    /**
     * 월 기준 주차 추출 (그 달의 몇 번째 주)
     */
    private int getWeekOfMonth(LocalDateTime createdAt) {
        WeekFields weekFields = WeekFields.of(Locale.KOREA);  // 월요일 시작
        return createdAt.get(weekFields.weekOfMonth());
    }


    private void checkStartCondition(StrategyState state) {
        if(state.equals(StrategyState.ONGOING)) {
            throw new BusinessException(InsightErrorCode.STRATEGY_ALREADY_STARTED);
        } else if(state.equals(StrategyState.COMPLETED)) {
            throw new BusinessException(InsightErrorCode.STRATEGY_ALREADY_COMPLETED);
        }
    }

    private void checkCompletionCondition(StrategyState state) {
        if(state.equals(StrategyState.COMPLETED)) {
            throw new BusinessException(InsightErrorCode.STRATEGY_ALREADY_COMPLETED);
        } else if(state.equals(StrategyState.BEFORE)) {
            throw new BusinessException(InsightErrorCode.STRATEGY_NOT_STARTED);
        }
    }

    private StrategyBriefResponse convertToStrategyBrief(Object strategy, StrategyType type) {
        if (strategy instanceof DangerMenuStrategy danger) {
            return new StrategyBriefResponse(
                    danger.getStrategyId(),
                    danger.getState(),
                    type,
                    danger.getSummary(),
                    danger.getDetail(),
                    danger.getStartDate(),
                    danger.getCompletionDate(),
                    danger.getCreatedAt()
            );
        } else if (strategy instanceof CautionMenuStrategy caution) {
            return new StrategyBriefResponse(
                    caution.getStrategyId(),
                    caution.getState(),
                    type,
                    caution.getSummary(),
                    caution.getDetail(),
                    caution.getStartDate(),
                    caution.getCompletionDate(),
                    caution.getCreatedAt()
            );
        } else if (strategy instanceof HighMarginMenuStrategy highMargin) {
            return new StrategyBriefResponse(
                    highMargin.getStrategyId(),
                    highMargin.getState(),
                    type,
                    highMargin.getSummary(),
                    highMargin.getDetail(),
                    highMargin.getStartDate(),
                    highMargin.getCompletionDate(),
                    highMargin.getCreatedAt()
            );
        }
        throw new BusinessException(InsightErrorCode.NOTFOUND_STRATEGY_TYPE);
    }

}
