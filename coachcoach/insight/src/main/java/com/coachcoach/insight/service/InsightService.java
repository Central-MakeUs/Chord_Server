package com.coachcoach.insight.service;

import com.coachcoach.common.api.CatalogQueryApi;
import com.coachcoach.common.api.UserQueryApi;
import com.coachcoach.common.dto.internal.StoreInfo;
import com.coachcoach.common.exception.BusinessException;
import com.coachcoach.insight.domain.*;
import com.coachcoach.insight.domain.enums.StrategyState;
import com.coachcoach.insight.domain.enums.StrategyType;
import com.coachcoach.insight.dto.response.*;
import com.coachcoach.insight.exception.InsightErrorCode;
import com.coachcoach.insight.repository.*;
import com.coachcoach.insight.util.DateCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InsightService {

    private final DangerMenuStrategyRepository dangerMenuStrategyRepository;
    private final CautionMenuStrategyRepository cautionMenuStrategyRepository;
    private final HighMarginMenuStrategyRepository highMarginMenuStrategyRepository;
    private final StrategyBaseLinesRepository strategyBaseLinesRepository;
    private final HighMarginMenuListRepository highMarginMenuListRepository;
    private final MenuSnapshotsRepository menuSnapshotsRepository;
    private final RecipeSnapshotsRepository recipeSnapshotsRepository;
    private final StrategyService strategyService;
    private final CatalogQueryApi catalogQueryApi;
    private final UserQueryApi userQueryApi;
    private final DateCalculator dateCalculator;

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
        LocalDate[] startAndEndOfWeek = dateCalculator.getStartAndEndOfWeek(year, month, weekOfMonth);
        LocalDate startDate = startAndEndOfWeek[0];
        LocalDate endDate = startAndEndOfWeek[1];

        // 해당 주 전략 baseline id
        List<StrategyBaselines> strategyBaseLine = strategyBaseLinesRepository.findByUserIdAndStrategyDateBetween(userId, startDate, endDate);
        List<Long> baselineIds = strategyBaseLine.stream().map(StrategyBaselines::getBaselineId).toList();
        Map<Long, StrategyBaselines> baselineMap =  strategyBaseLine.stream()
                .collect(
                        Collectors.toMap(
                                StrategyBaselines::getBaselineId,
                                Function.identity()
                        )
                );

        List<Strategy> all = strategyService.findByBaselineIdIn(baselineIds);

        // 정렬
        Comparator<Strategy> strategyComparator = Comparator
                // 상태 별 그룹 정렬
                .<Strategy, Integer>comparing(s -> switch (s.getState()) {
                    case ONGOING -> 0;
                    case BEFORE -> 1;
                    case COMPLETED -> 2;
                    default -> 4;
                })
                // 진행 중: startDate 내림차순
                .thenComparing(s -> {
                    if(s.getState() == StrategyState.ONGOING) {
                        return s.getState() != null ? s.getStartDate() : LocalDateTime.MIN;
                    }

                    return LocalDateTime.MAX;
                }, Comparator.reverseOrder())
                // 진행 전: 위험 -> 주의 -> 고마진 순
                .thenComparing(s -> {
                    if (s.getState() == StrategyState.BEFORE) {
                        return switch (s.getType()) {
                            case DANGER -> 0;
                            case CAUTION -> 1;
                            case HIGH_MARGIN -> 2;
                            default -> 3;
                        };
                    }
                    return 0;
                })
                // 진행완료: 실행 완료 버튼 누른 최근 순
                .thenComparing(s -> {
                    if (s.getState() == StrategyState.COMPLETED) {
                        return s.getCompletionDate() != null ? s.getCompletionDate() : LocalDateTime.MIN;
                    }
                    return LocalDateTime.MAX;
                }, Comparator.reverseOrder());

        return all.stream()
                .sorted(strategyComparator)
                .map(s -> {
                    StrategyBaselines b = baselineMap.get(s.getBaselineId());

                    return StrategyBriefResponse.builder()
                            .menuId(s.getMenuId())
                            .strategyId(s.getStrategyId())
                            .state(s.getState())
                            .type(s.getType())
                            .title(
                                    (s.getType() == StrategyType.HIGH_MARGIN)
                                            ? dateCalculator.getMonth(b.getStrategyDate()) + "월" + dateCalculator.getWeekOfMonth(b.getStrategyDate()) + "주 고마진 메뉴"
                                            : s.getMenuSnapshot().getMenuName()
                            )
                            .summary(s.getSummary())
                            .detail(s.getDetail())
                            .startDate(s.getStartDate())
                            .createdAt(s.getCreatedAt())
                            .build();
                }).toList();
    }

    /**
     * 내가 저장한 전략 모음
     * 필터링 기준: (년+월) + 실행 완료/미완료
     * 실행 완료 기준: 실행 중 + 실행 완료
     * 정렬: 생성 날짜 내림차순
     */
    public List<SavedStrategyResponse> getSavedStrategies(Long userId, Integer year, Integer month, Boolean isCompleted) {
        // 주의 시작일, 끝일
        LocalDate[] startAndEndOfMonth = dateCalculator.getStartAndEndOfMonth(year, month);
        LocalDate startDate = startAndEndOfMonth[0];
        LocalDate endDate = startAndEndOfMonth[1];

        List<StrategyBaselines> strategyBaseLine = strategyBaseLinesRepository.findByUserIdAndStrategyDateBetween(userId, startDate, endDate);
        List<Long> baseLineIds = strategyBaseLine.stream().map(StrategyBaselines::getBaselineId).toList();

        Map<Long, StrategyBaselines> baselineMap =  strategyBaseLine.stream()
                .collect(
                        Collectors.toMap(
                                StrategyBaselines::getBaselineId,
                                Function.identity()
                        )
                );
        List<StrategyState> states = (isCompleted) ? List.of(StrategyState.COMPLETED, StrategyState.ONGOING) : List.of(StrategyState.BEFORE);

        List<Strategy> all = strategyService.findBySavedTrueAndBaselineIdInAndStateIn(baseLineIds, states);

        // 정렬
        Comparator<Strategy> strategyComparator = Comparator
                // 상태 별 그룹 정렬
                .<Strategy, Integer>comparing(s -> switch (s.getState()) {
                    case ONGOING -> 0;
                    case BEFORE -> 1;
                    case COMPLETED -> 2;
                    default -> 4;
                })
                // 진행 중: startDate 내림차순
                .thenComparing(s -> {
                    if(s.getState() == StrategyState.ONGOING) {
                        return s.getState() != null ? s.getStartDate() : LocalDateTime.MIN;
                    }

                    return LocalDateTime.MAX;
                }, Comparator.reverseOrder())
                // 진행 전: 위험 -> 주의 -> 고마진 순
                .thenComparing(s -> {
                    if (s.getState() == StrategyState.BEFORE) {
                        return switch (s.getType()) {
                            case DANGER -> 0;
                            case CAUTION -> 1;
                            case HIGH_MARGIN -> 2;
                            default -> 3;
                        };
                    }
                    return 0;
                })
                // 진행완료: 실행 완료 버튼 누른 최근 순
                .thenComparing(s -> {
                    if (s.getState() == StrategyState.COMPLETED) {
                        return s.getCompletionDate() != null ? s.getCompletionDate() : LocalDateTime.MIN;
                    }
                    return LocalDateTime.MAX;
                }, Comparator.reverseOrder());

        return all.stream()
                .sorted(strategyComparator)
                .map(s -> {
                    StrategyBaselines b = baselineMap.get(s.getBaselineId());

                    if (b == null) {
                        throw new BusinessException(InsightErrorCode.NOTFOUND_STRATEGY_BASELINE);
                    }
                    return SavedStrategyResponse.builder()
                            .strategyId(s.getStrategyId())
                            .state(s.getState())
                            .type(s.getType())
                            .summary(s.getSummary())
                            .detail(s.getDetail())
                            .year(dateCalculator.getYear(b.getStrategyDate()))
                            .month(dateCalculator.getMonth(b.getStrategyDate()))
                            .weekOfMonth(dateCalculator.getWeekOfMonth(b.getStrategyDate()))
                            .menuId(s.getMenuId())
                            .title(
                                    (s.getType() == StrategyType.HIGH_MARGIN)
                                            ? dateCalculator.getMonth(b.getStrategyDate()) + "월" + dateCalculator.getWeekOfMonth(b.getStrategyDate()) + "주 고마진 메뉴"
                                            : s.getMenuSnapshot().getMenuName()
                            )
                            .createdAt(s.getCreatedAt())
                            .strategyDate(b.getStrategyDate())
                            .build();
                })
                .toList();
    }

    /**
     * 위험 메뉴 전략 상세
     */
    @Transactional(transactionManager = "transactionManager")
    public DangerMenuStrategyDetailResponse getDangerMenuStrategyDetail(Long userId, Long strategyId) {
        DangerMenuStrategy strategy = dangerMenuStrategyRepository.findByUserIdAndStrategyId(userId, strategyId)
                .orElseThrow(() -> new BusinessException(InsightErrorCode.NOTFOUND_STRATEGY));
        StrategyBaselines baselines = strategyBaseLinesRepository.findById(strategy.getBaselineId())
                .orElseThrow(() -> new BusinessException(InsightErrorCode.NOTFOUND_STRATEGY_BASELINE));

        return new DangerMenuStrategyDetailResponse(
                strategy.getStrategyId(),
                strategy.getSummary(),
                strategy.getDetail(),
                strategy.getGuide(),
                strategy.getExpectedEffect(),
                strategy.getState(),
                strategy.getStartDate(),
                strategy.getCompletionDate(),
                strategy.getMenuId(),
                strategy.getMenuSnapshot().getMenuName(),
                strategy.getMenuSnapshot().getCostRate(),
                strategy.getType(),
                dateCalculator.getYear(baselines.getStrategyDate()),
                dateCalculator.getMonth(baselines.getStrategyDate()),
                dateCalculator.getWeekOfMonth(baselines.getStrategyDate())
        );
    }

    /**
     * 주의 메뉴 전략 상세
     */
    @Transactional(transactionManager = "transactionManager")
    public CautionMenuStrategyDetailResponse getCautionMenuStrategyDetail(Long userId, Long strategyId) {
        CautionMenuStrategy strategy = cautionMenuStrategyRepository.findByUserIdAndStrategyId(userId, strategyId)
                .orElseThrow(() -> new BusinessException(InsightErrorCode.NOTFOUND_STRATEGY));
        StrategyBaselines baselines = strategyBaseLinesRepository.findById(strategy.getBaselineId())
                .orElseThrow(() -> new BusinessException(InsightErrorCode.NOTFOUND_STRATEGY_BASELINE));

        return new CautionMenuStrategyDetailResponse(
                strategy.getStrategyId(),
                strategy.getSummary(),
                strategy.getDetail(),
                strategy.getGuide(),
                strategy.getExpectedEffect(),
                strategy.getState(),
                strategy.getStartDate(),
                strategy.getCompletionDate(),
                strategy.getMenuId(),
                strategy.getMenuSnapshot().getMenuName(),
                strategy.getMenuSnapshot().getCostRate(),
                strategy.getType(),
                dateCalculator.getYear(baselines.getStrategyDate()),
                dateCalculator.getMonth(baselines.getStrategyDate()),
                dateCalculator.getWeekOfMonth(baselines.getStrategyDate())
        );
    }

    /**
     * 고마진 메뉴 추천 전략 상세
     */
    public HighMarginMenuStrategyDetailResponse getHighMarginMenuStrategyDetail(Long userId, Long strategyId) {
        HighMarginMenuStrategy strategy = highMarginMenuStrategyRepository.findByUserIdAndStrategyId(userId, strategyId)
                .orElseThrow(() -> new BusinessException(InsightErrorCode.NOTFOUND_STRATEGY));
        StrategyBaselines baselines = strategyBaseLinesRepository.findById(strategy.getBaselineId())
                .orElseThrow(() -> new BusinessException(InsightErrorCode.NOTFOUND_STRATEGY_BASELINE));
        List<HighMarginMenuList> menuList = highMarginMenuListRepository.findByStrategyId(strategy.getStrategyId());

        return HighMarginMenuStrategyDetailResponse
                .builder()
                .strategyId(strategy.getStrategyId())
                .summary(strategy.getSummary())
                .detail(strategy.getDetail())
                .guide(strategy.getGuide())
                .expectedEffect(strategy.getExpectedEffect())
                .state(strategy.getState())
                .startDate(strategy.getStartDate())
                .completionDate(strategy.getCompletionDate())
                .type(strategy.getType())
                .year(dateCalculator.getYear(baselines.getStrategyDate()))
                .month(dateCalculator.getMonth(baselines.getStrategyDate()))
                .weekOfMonth(dateCalculator.getWeekOfMonth(baselines.getStrategyDate()))
                .menuNames(menuList.stream().map(m -> m.getMenuSnapshot().getMenuName()).toList())
                .build();
    }
    
    /**
     * 전략 시작
     */
    @Transactional(transactionManager = "transactionManager")
    public void changeStateToOngoing(Long userId, Long strategyId, StrategyType strategyType) {
        Strategy strategy = strategyService.findByUserIdAndStrategyId(userId, strategyId, strategyType);
        dateCalculator.checkStartCondition(strategy.getState());
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

        // 완료로 업데이트
        dateCalculator.checkCompletionCondition(strategy.getState());
        strategy.updateStateToCompleted();

        // 개선된 평균 마진률 계산
        BigDecimal marginRateImprovement = baseline.getAvgMarginRate().subtract(avgMarginRate);

        return new CompletionPhraseResponse(strategyService.getCompletionPhrase(strategy, strategy.getMenuSnapshot(), storeInfo, marginRateImprovement));
    }

    @Transactional(transactionManager = "transactionManager")
    public CompletionPhraseResponse changeStateToCompletedWithoutCheck(Long userId, Long strategyId, StrategyType strategyType) {
        StoreInfo storeInfo = userQueryApi.findStoreByUserId(userId);
        BigDecimal avgMarginRate = catalogQueryApi.getAvgMarginRate(userId);

        // 전략 조회
        Strategy strategy = strategyService.findByUserIdAndStrategyId(userId, strategyId, strategyType);

        // 전략 Baseline 조회
        StrategyBaselines baseline = strategyBaseLinesRepository.findById(strategy.getBaselineId())
                .orElseThrow(() -> new BusinessException(InsightErrorCode.NOTFOUND_STRATEGY_BASELINE));

        // 완료로 업데이트
        strategy.updateStateToCompleted();

        // 개선된 평균 마진률 계산
        BigDecimal marginRateImprovement = baseline.getAvgMarginRate().subtract(avgMarginRate);

        return new CompletionPhraseResponse(strategyService.getCompletionPhrase(strategy, strategy.getMenuSnapshot(), storeInfo, marginRateImprovement));
    }

    /*---- 홈화면 ----*/
    public HomeStrategiesResponse getHomeStrategies(Long userId, int year, int month, int weekOfMonth) {
        LocalDate[] startAndEndOfWeek = dateCalculator.getStartAndEndOfWeek(year, month, weekOfMonth);
        LocalDate startDate = startAndEndOfWeek[0];
        LocalDate endDate = startAndEndOfWeek[1];

        // baseline 조회
        List<StrategyBaselines> baselines = strategyBaseLinesRepository.findByUserIdAndStrategyDateBetween(userId, startDate, endDate);
        List<Long> baselineIds = baselines.stream().map(StrategyBaselines::getBaselineId).toList();
        Map<Long, StrategyBaselines> baselineMap =  baselines.stream()
                .collect(
                        Collectors.toMap(
                                StrategyBaselines::getBaselineId,
                                Function.identity()
                        )
                );

        List<Strategy> all = strategyService.findByBaselineIdIn(baselineIds);


        // 정렬
        Comparator<Strategy> strategyComparator = Comparator
                // 상태 별 그룹 정렬
                .<Strategy, Integer>comparing(s -> switch (s.getState()) {
                    case ONGOING -> 0;
                    case BEFORE -> 1;
                    case COMPLETED -> 2;
                    default -> 4;
                })
                // 진행 중: startDate 내림차순
                .thenComparing(s -> {
                    if(s.getState() == StrategyState.ONGOING) {
                        return s.getState() != null ? s.getStartDate() : LocalDateTime.MIN;
                    }

                    return LocalDateTime.MAX;
                }, Comparator.reverseOrder())
                // 진행 전: 위험 -> 주의 -> 고마진 순
                .thenComparing(s -> {
                    if (s.getState() == StrategyState.BEFORE) {
                        return switch (s.getType()) {
                            case DANGER -> 0;
                            case CAUTION -> 1;
                            case HIGH_MARGIN -> 2;
                            default -> 3;
                        };
                    }
                    return 0;
                })
                // 진행완료: 실행 완료 버튼 누른 최근 순
                .thenComparing(s -> {
                    if (s.getState() == StrategyState.COMPLETED) {
                        return s.getCompletionDate() != null ? s.getCompletionDate() : LocalDateTime.MIN;
                    }
                    return LocalDateTime.MAX;
                }, Comparator.reverseOrder());
        List<HomeStrategyBrief> sorted = all.stream()
                .sorted(strategyComparator)
                .map(s -> {
                    StrategyBaselines b = baselineMap.get(s.getBaselineId());

                    if(b == null) throw new BusinessException(InsightErrorCode.NOTFOUND_STRATEGY_BASELINE);

                    return HomeStrategyBrief.builder()
                            .menuId(s.getMenuId())
                            .strategyId(s.getStrategyId())
                            .state(s.getState())
                            .type(s.getType())
                            .title(
                                    (s.getType() == StrategyType.HIGH_MARGIN)
                                            ? dateCalculator.getMonth(b.getStrategyDate()) + "월" + dateCalculator.getWeekOfMonth(b.getStrategyDate()) + "주 고마진 메뉴"
                                            : s.getMenuSnapshot().getMenuName()
                            )
                            .summary(s.getSummary())
                            .createdAt(s.getCreatedAt())
                            .build();

                })
                .toList();

        return new HomeStrategiesResponse(sorted);
    }

    /**
     * 진단이 필요한 메뉴들 전략 카드 조회
     */
    public NeedManagement getNeedManagement(Long userId) {
        LocalDate[] startAndEndOfWeekByCurrentTime = dateCalculator.getStartAndEndOfWeekByCurrentTime();
        LocalDate startDate = startAndEndOfWeekByCurrentTime[0];
        LocalDate endDate = startAndEndOfWeekByCurrentTime[1];

        List<StrategyBaselines> baselines = strategyBaseLinesRepository.findByUserIdAndStrategyDateBetween(userId, startDate, endDate);

        if(baselines == null || baselines.isEmpty()) {
            return new NeedManagement(null, List.of());
        }

        List<Long> baselineIds = baselines.stream().map(StrategyBaselines::getBaselineId).toList();

        List<DangerMenuStrategy> strategies = dangerMenuStrategyRepository.findByBaselineIdIn(baselineIds);

        List<NeedManagementMenu> menus = strategies.stream()
                .map(
                        strategy -> {
                            MenuSnapshots menuInfo = strategy.getMenuSnapshot();

                            return NeedManagementMenu.builder()
                                    .strategyId(strategy.getStrategyId())
                                    .menuId(menuInfo.getMenuId())
                                    .menuName(menuInfo.getMenuName())
                                    .costRate(menuInfo.getCostRate())
                                    .marginRate(menuInfo.getMarginRate())
                                    .marginGradeCode(menuInfo.getMarginGradeCode())
                                    .state(strategy.getState())
                                    .build();
                        }
                )
                .toList();

        return new NeedManagement(
                baselines.get(0).getStrategyDate(),
                menus
        );
    }
}
