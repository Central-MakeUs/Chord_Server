package com.coachcoach.insight.util;

import com.coachcoach.common.exception.BusinessException;
import com.coachcoach.insight.domain.enums.StrategyState;
import com.coachcoach.insight.exception.InsightErrorCode;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.Locale;

@Component
public class DateCalculator {
    /**
     * N월 N주차에 해당하는 시작일(월)과 끝일(일) 반환
     * @param year
     * @param month
     * @param weekOfMonth
     */
    public LocalDate[] getStartAndEndOfWeek(int year, int month, int weekOfMonth) {
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

    public LocalDate[] getStartAndEndOfWeekByCurrentTime() {
        LocalDate today = LocalDate.now();

        // 이번 주 월요일
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);

        // 이번 주 일요일
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);

        return new LocalDate[]{startOfWeek, endOfWeek};
    }

    /**
     * 특정 년월의 시작일과 끝일 반환
     * @param year 년도
     * @param month 월
     * @return [시작일, 끝일]
     */
    public LocalDate[] getStartAndEndOfMonth(int year, int month) {
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
    public int getYear(LocalDateTime createdAt) {
        return createdAt.getYear();
    }
    public int getYear(LocalDate strategyDate) {
        return strategyDate.getYear();
    }

    /**
     * 월 추출
     */
    public int getMonth(LocalDateTime createdAt) {
        return createdAt.getMonthValue();
    }
    public int getMonth(LocalDate strategyDate) {
        return strategyDate.getMonthValue();
    }

    /**
     * 월 기준 주차 추출 (그 달의 몇 번째 주)
     */
    public int getWeekOfMonth(LocalDateTime createdAt) {
        WeekFields weekFields = WeekFields.of(Locale.KOREA);  // 월요일 시작
        return createdAt.get(weekFields.weekOfMonth());
    }
    public int getWeekOfMonth(LocalDate strategyDate) {
        WeekFields weekFields = WeekFields.of(Locale.KOREA);
        return strategyDate.get(weekFields.weekOfMonth());
    }


    public void checkStartCondition(StrategyState state) {
        if(state.equals(StrategyState.ONGOING)) {
            throw new BusinessException(InsightErrorCode.STRATEGY_ALREADY_STARTED);
        } else if(state.equals(StrategyState.COMPLETED)) {
            throw new BusinessException(InsightErrorCode.STRATEGY_ALREADY_COMPLETED);
        }
    }

    public void checkCompletionCondition(StrategyState state) {
        if(state.equals(StrategyState.COMPLETED)) {
            throw new BusinessException(InsightErrorCode.STRATEGY_ALREADY_COMPLETED);
        } else if(state.equals(StrategyState.BEFORE)) {
            throw new BusinessException(InsightErrorCode.STRATEGY_NOT_STARTED);
        }
    }
}
