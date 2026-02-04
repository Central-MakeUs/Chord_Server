package com.coachcoach.api.insight;

import com.coachcoach.common.security.userdetails.CustomUserDetails;
import com.coachcoach.insight.domain.enums.StrategyType;
import com.coachcoach.insight.dto.response.CompletionPhraseResponse;
import com.coachcoach.insight.dto.response.HomeStrategyCardResponse;
import com.coachcoach.insight.service.InsightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "전략", description = "전략 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/insights")
public class InsightController {

    private final InsightService insightService;

    @Operation(summary = "홈화면 - 진단 필요 메뉴 전략 리스트")
    @GetMapping("/home/strategies")
    public HomeStrategyCardResponse onboarding(
            @AuthenticationPrincipal CustomUserDetails details,
            @RequestParam(name = "year") int year,
            @RequestParam(name = "month") int month,
            @RequestParam(name = "weekOfMonth") int weekOfMonth
    ) {
        return insightService.getStrategiesOfDangerMenus(Long.valueOf(details.getUserId()), year, month, weekOfMonth);
    }

    @Operation(summary = "전략 상태 변경 (실행 전 -> 실행 중)")
    @PatchMapping("/strategies/{strategyId}/start")
    public void changeStateToOngoing(
            @AuthenticationPrincipal CustomUserDetails details,
            @PathVariable(name = "strategyId") Long strategyId,
            @RequestParam(name = "strategyType") StrategyType strategyType
    ) {
        insightService.changeStateToOngoing(Long.valueOf(details.getUserId()), strategyId, strategyType);
    }

    @Operation(summary = "전략 상태 변경 (실행 중 -> 실행 후)")
    @PatchMapping("/strategies/{strategyId}/complete")
    public CompletionPhraseResponse changeStateToCompleted(
            @AuthenticationPrincipal CustomUserDetails details,
            @PathVariable(name = "strategyId") Long strategyId,
            @RequestParam(name = "strategyType") StrategyType strategyType
    ) {
        return insightService.changeStateToCompleted(Long.valueOf(details.getUserId()), strategyId, strategyType);
    }

    @Operation(summary = "전략 저장/해제")
    @PatchMapping("/strategies/{strategyId}/save")
    public void saveStrategy(
            @AuthenticationPrincipal CustomUserDetails details,
            @PathVariable(name = "strategyId") Long strategyId,
            @RequestParam(name = "strategyType") StrategyType strategyType,
            @RequestParam(name = "saved") Boolean saved
    ) {
        insightService.saveStrategy(Long.valueOf(details.getUserId()), strategyId, strategyType, saved);
    }
}
