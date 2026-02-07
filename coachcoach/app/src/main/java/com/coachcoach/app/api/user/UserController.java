package com.coachcoach.app.api.user;

import com.coachcoach.common.security.userdetails.CustomUserDetails;
import com.coachcoach.user.dto.request.OnboardingRequest;
import com.coachcoach.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "유저", description = "유저 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @Operation(summary = "온보딩")
    @PatchMapping("/onboarding")
    public void onboarding(
            @AuthenticationPrincipal CustomUserDetails details,
            @Valid @RequestBody OnboardingRequest request
    ) {
        userService.onboarding(Long.valueOf(details.getUserId()), request);
    }
}
