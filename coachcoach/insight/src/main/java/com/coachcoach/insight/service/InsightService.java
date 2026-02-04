package com.coachcoach.insight.service;

import com.coachcoach.common.api.CatalogQueryApi;
import com.coachcoach.insight.domain.DangerMenuStrategy;
import com.coachcoach.insight.dto.response.DangerMenuBriefCard;
import com.coachcoach.insight.dto.response.HomeStrategyCardResponse;
import com.coachcoach.insight.repository.DangerMenuStrategyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class InsightService {

    private final DangerMenuStrategyRepository dangerMenuStrategyRepository;
    private final CatalogQueryApi catalogQueryApi;

    /**
     * 홈화면 - 위험 메뉴 전략 카드 & 진단 필요 메뉴 개수 노출 (최대 3개) / 실행
     */
    public HomeStrategyCardResponse getStrategiesOfDangerMenus(Long userId, int year, int month, int weekOfMonth) {
        LocalDate[] startAndEndDate = getStartAndEndOfWeek(year, month, weekOfMonth);

        log.info("{}년 {}월 {}주차({} - {}) 위험 메뉴 전략 카드 리스트", year, month, weekOfMonth, startAndEndDate[0], startAndEndDate[1]);

        // 진단이 필요한 메뉴 (위험 메뉴) 개수
        int numOfDangerMenus = catalogQueryApi.countByUserIdAndMarginGradeCode(userId, "DANGER");

        // 전략 카드 목록
        List<DangerMenuStrategy> strategies = dangerMenuStrategyRepository
                .findByUserIdAndStrategyDateBetweenOrderByStrategyId(userId, startAndEndDate[0], startAndEndDate[1]);


        List<DangerMenuBriefCard> listOfStrategies = strategies.stream()
                .map(DangerMenuBriefCard::from)
                .toList();

        return new HomeStrategyCardResponse(
                numOfDangerMenus,
                listOfStrategies
        );
    }

    /**
     * 이번주 추천 전략 (메뉴명 + 한줄요약) 리스트
     */

    /**
     * 내가 저장한 전략 리스트 (메뉴명 + 한줄 요약) 리스트
     */

    /**
     * 전략 카드 상세
     */

    /**
     * 전략 상태 변경 (실행 전 -> 실행 중)
     */

    /**
     * 전략 상태 변경 (실행 중 -> 실행 완료)
     * 실행 완료 문구 노출
     */

    /**
     * 전략 저장
     */

    /**
     * 주차 별 전략 리스트
     * 정렬: 실행 중인 전략 -> 실행 전 전략 -> 실행 완료 전략
     */

    /**
     * 내가 저장한 전략 리스트
     * 0월 0주차 + 한 줄 요약 + 실행 여부
     * 정렬: 실행 중 -> 전 -> 완
     */

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
}
