package com.coachcoach.app.api.user;

import com.coachcoach.user.security.CustomUserDetails;
import com.coachcoach.user.dto.request.*;
import com.coachcoach.user.service.AuthService;
import com.coachcoach.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "인증/인가 V2", description = "인증/인가 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/auth")
public class AuthControllerV2 {

    private final AuthService authService;
    private final NotificationService notificationService;

    /**
     * 로그아웃
     */
    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public void logout(
            @AuthenticationPrincipal CustomUserDetails details,
            @RequestBody LogoutWithRefreshTokenRequest request
    ) {
        authService.logoutWithRefreshToken(Long.valueOf(details.getUserId()), request);
    }
}
