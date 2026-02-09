package com.coachcoach.insight.service;

import com.coachcoach.common.api.CatalogQueryApi;
import com.coachcoach.common.exception.BusinessException;
import com.coachcoach.insight.domain.CautionMenuStrategy;
import com.coachcoach.insight.domain.DangerMenuStrategy;
import com.coachcoach.insight.domain.HighMarginMenuStrategy;
import com.coachcoach.insight.domain.StrategyBaselines;
import com.coachcoach.insight.domain.enums.StrategyState;
import com.coachcoach.insight.domain.enums.StrategyType;
import com.coachcoach.insight.dto.response.CompletionPhraseResponse;
import com.coachcoach.insight.dto.response.SavedStrategyResponse;
import com.coachcoach.insight.dto.response.StrategyBriefResponse;
import com.coachcoach.insight.exception.InsightErrorCode;
import com.coachcoach.insight.repository.CautionMenuStrategyRepository;
import com.coachcoach.insight.repository.DangerMenuStrategyRepository;
import com.coachcoach.insight.repository.HighMarginMenuStrategyRepository;
import com.coachcoach.insight.repository.StrategyBaseLinesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InsightService {

    private final DangerMenuStrategyRepository dangerMenuStrategyRepository;
    private final CautionMenuStrategyRepository cautionMenuStrategyRepository;
    private final HighMarginMenuStrategyRepository highMarginMenuStrategyRepository;
    private final StrategyBaseLinesRepository strategyBaseLinesRepository;
    private final CatalogQueryApi catalogQueryApi;

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
        if(StrategyType.DANGER.equals(type)) {
            DangerMenuStrategy strategy = dangerMenuStrategyRepository.findByUserIdAndStrategyId(userId, strategyId)
                    .orElseThrow(() -> new BusinessException(InsightErrorCode.NOTFOUND_STRATEGY));
            strategy.updateSaved(save);
            return;
        } else if(StrategyType.CAUTION.equals(type)) {
            CautionMenuStrategy strategy = cautionMenuStrategyRepository.findByUserIdAndStrategyId(userId, strategyId)
                    .orElseThrow(() -> new BusinessException(InsightErrorCode.NOTFOUND_STRATEGY));
            strategy.updateSaved(save);
            return;
        } else if(StrategyType.HIGH_MARGIN.equals(type)) {
            HighMarginMenuStrategy strategy = highMarginMenuStrategyRepository.findByUserIdAndStrategyId(userId, strategyId)
                    .orElseThrow(() -> new BusinessException(InsightErrorCode.NOTFOUND_STRATEGY));
            strategy.updateSaved(save);
            return;
        }
        throw new BusinessException(InsightErrorCode.NOTFOUND_STRATEGY_TYPE);
    }

    /**
     * 전략 시작
     */

    /**
     * 전략 시작
     */
    @Transactional(transactionManager = "transactionManager")
    public void changeStateToOngoing(Long userId, Long strategyId, StrategyType strategyType) {

        if(strategyType.equals(StrategyType.DANGER)) {
            // type == DANGER
            DangerMenuStrategy strategy = dangerMenuStrategyRepository.findByUserIdAndStrategyId(userId, strategyId)
                    .orElseThrow(() -> new BusinessException(InsightErrorCode.NOTFOUND_STRATEGY));
            checkStartCondition(strategy.getState());
            strategy.updateStateToOngoing();
            return;
        } else if(strategyType.equals(StrategyType.CAUTION)) {
            // type == CAUTION
            CautionMenuStrategy strategy = cautionMenuStrategyRepository.findByUserIdAndStrategyId(userId, strategyId)
                    .orElseThrow(() -> new BusinessException(InsightErrorCode.NOTFOUND_STRATEGY));
            checkStartCondition(strategy.getState());
            strategy.updateStateToOngoing();
            return;
        } else if(strategyType.equals(StrategyType.HIGH_MARGIN)) {
            // type == HIGH_MARGIN
            HighMarginMenuStrategy strategy = highMarginMenuStrategyRepository.findByUserIdAndStrategyId(userId, strategyId)
                    .orElseThrow(() -> new BusinessException(InsightErrorCode.NOTFOUND_STRATEGY));
            checkStartCondition(strategy.getState());
            strategy.updateStateToOngoing();
            return;
        }
        throw new BusinessException(InsightErrorCode.NOTFOUND_STRATEGY_TYPE);
    }

    /**
     * 전략 완료
     * 조건: state == "ongoing"
     */
//    @Transactional(transactionManager = "transactionManager")
//    public CompletionPhraseResponse changeStateToCompleted(Long userId, Long strategyId, StrategyType strategyType) {
//        StringBuilder completionPhrase = new StringBuilder();
//
//        if(strategyType.equals(StrategyType.DANGER)) {
//            // type == DANGER
//            DangerMenuStrategy strategy = dangerMenuStrategyRepository.findByUserIdAndStrategyId(userId, strategyId)
//                    .orElseThrow(() -> new BusinessException(InsightErrorCode.NOTFOUND_STRATEGY));
//            checkCompletionCondition(strategy.getState());
//            strategy.updateStateToCompleted();
//
//            if(strategy.getGuideCode().equals("REMOVE_MENU")) {
//                String completionPhraseTemplate = "좋은 판단이에요. 이 조치는 카페 수익 구조를 분명히 개선했어요. {메뉴명}은 이전 구조에서는 판매될수록 전체 수익에 부담이 되는 메뉴였어요. 이번 전략을 적용하면서, {카페명}의 평균 마진률이 약 {}%p 개선되었고, 이 메뉴가 전체 수익성에 미치던 영향도 줄어들었어요. 같은 매출을 만들더라도, 이전보다 더 남는 구조에 가까워졌어요.";
//            } else if(strategy.getGuideCode().equals("ADJUST_PRICE")) {
//
//            }
//
//            completionPhrase.append("");
//        } else if(strategyType.equals(StrategyType.CAUTION)) {
//            // type == CAUTION
//            CautionMenuStrategy strategy = cautionMenuStrategyRepository.findByUserIdAndStrategyId(userId, strategyId)
//                    .orElseThrow(() -> new BusinessException(InsightErrorCode.NOTFOUND_STRATEGY));
//            checkCompletionCondition(strategy.getState());
//            strategy.updateStateToCompleted();
//            completionPhrase.append("");
//        } else if(strategyType.equals(StrategyType.HIGH_MARGIN)) {
//            // type == HIGH_MARGIN
//            HighMarginMenuStrategy strategy = highMarginMenuStrategyRepository.findByUserIdAndStrategyId(userId, strategyId)
//                    .orElseThrow(() -> new BusinessException(InsightErrorCode.NOTFOUND_STRATEGY));
//            checkCompletionCondition(strategy.getState());
//            strategy.updateStateToCompleted();
//            completionPhrase.append(strategy.getCompletionPhrase());
//        }
//
//        return new CompletionPhraseResponse(completionPhrase.toString());
//    }
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
