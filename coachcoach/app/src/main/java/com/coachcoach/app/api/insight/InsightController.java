package com.coachcoach.app.api.insight;

import com.coachcoach.common.security.userdetails.CustomUserDetails;
import com.coachcoach.insight.domain.enums.StrategyType;
import com.coachcoach.insight.dto.response.SavedStrategyResponse;
import com.coachcoach.insight.dto.response.StrategyBriefResponse;
import com.coachcoach.insight.service.InsightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "전략", description = "전략 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/insights")
public class InsightController {

    private final InsightService insightService;


    /**
     * 이번주 추천 전략
     */
    @Operation(summary = "이번주 추천 전략 조회", description = "특정 주차의 추천 전략 목록을 조회합니다")
    @GetMapping("/strategies/weekly")
    public List<StrategyBriefResponse> getWeeklyRecommendedStrategies(
            @AuthenticationPrincipal CustomUserDetails details,
            @RequestParam(name = "year") int year,
            @RequestParam(name = "month") int month,
            @RequestParam(name = "weekOfMonth") int weekOfMonth
    ) {
        return insightService.getWeeklyRecommendedStrategies(Long.valueOf(details.getUserId()), year, month, weekOfMonth);
    }

    /**
     * 내가 저장한 전략 모음
     */
    @Operation(summary = "저장한 전략 조회")
    @GetMapping("/strategies/saved")
    public List<SavedStrategyResponse> getSavedStrategies(
            @AuthenticationPrincipal CustomUserDetails details,
            @RequestParam(name = "year") Integer year,
            @RequestParam(name = "month") Integer month,
            @RequestParam(name = "isCompleted") Boolean isCompleted
    ) {
        return insightService.getSavedStrategies(Long.valueOf(details.getUserId()), year, month, isCompleted);
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
    @Operation(summary = "전략 저장/해제")
    @PatchMapping("/strategies/{strategyId}/save")
    public void toggleStrategySaved(
            @AuthenticationPrincipal CustomUserDetails details,
            @PathVariable Long strategyId,
            @RequestParam StrategyType type,
            @RequestParam boolean isSaved
    ) {
        insightService.toggleStrategySaved(strategyId, type, Long.valueOf(details.getUserId()), isSaved);
    }

    /**
     * 전략 시작
     */

    /**
     * 전략 완료
     */
}
