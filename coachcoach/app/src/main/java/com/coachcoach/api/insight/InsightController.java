package com.coachcoach.api.insight;

import com.coachcoach.common.security.userdetails.CustomUserDetails;
import com.coachcoach.insight.dto.response.HomeStrategyCardResponse;
import com.coachcoach.insight.service.InsightService;
import com.coachcoach.user.dto.request.OnboardingRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
    @PatchMapping("/home/danger")
    public HomeStrategyCardResponse onboarding(
            @AuthenticationPrincipal CustomUserDetails details,
            @RequestParam(name = "year") int year,
            @RequestParam(name = "month") int month,
            @RequestParam(name = "weekOfMonth") int weekOfMonth
    ) {
        return insightService.getStrategiesOfDangerMenus(Long.valueOf(details.getUserId()), year, month, weekOfMonth);
    }
}
