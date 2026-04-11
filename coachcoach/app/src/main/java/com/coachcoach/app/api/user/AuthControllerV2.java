package com.coachcoach.app.api.user;

import com.coachcoach.common.exception.BusinessException;
import com.coachcoach.common.security.userdetails.CustomUserDetails;
import com.coachcoach.user.dto.request.*;
import com.coachcoach.user.dto.response.LoginResponse;
import com.coachcoach.user.dto.response.TokenRefreshResponse;
import com.coachcoach.user.exception.SocialLoginErrorCode;
import com.coachcoach.user.service.AuthService;
import com.coachcoach.user.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
