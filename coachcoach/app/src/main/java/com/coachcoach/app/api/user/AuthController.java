package com.coachcoach.app.api.user;

import com.coachcoach.common.exception.BusinessException;
import com.coachcoach.common.security.userdetails.CustomUserDetails;
import com.coachcoach.user.dto.request.*;
import com.coachcoach.user.dto.response.LoginResponse;
import com.coachcoach.user.dto.response.TokenRefreshResponse;
import com.coachcoach.user.exception.SocialLoginErrorCode;
import com.coachcoach.user.exception.UserErrorCode;
import com.coachcoach.user.service.AuthService;
import com.coachcoach.user.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "인증/인가", description = "인증/인가 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final NotificationService notificationService;

    /**
     * 회원가입
     */
    @Operation(summary = "회원가입")
    @PostMapping("/sign-up")
    public void signUp(
            @Valid @RequestBody SignUpRequest request
    ) {
        authService.signUp(request);
    }

    /**
     * 로그인
     */
    @Operation(summary = "로그인")
    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request
    ) {
        return authService.login(request);
    }

    /**
     * new access token 발급 요청
     */
    @Operation(summary = "토큰 refresh 요청")
    @PostMapping("/refresh")
    public TokenRefreshResponse refreshToken(
            @Valid @RequestBody TokenRefreshRequest request
    ) {
        return authService.refreshToken(request);
    }

    /**
     * 로그아웃
     */
    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public void logout(
            @AuthenticationPrincipal CustomUserDetails details,
            @RequestBody LogoutRequest request
    ) {
        authService.logout(Long.valueOf(details.getUserId()), request);
    }

    /**
     * Fcm token 저장
     */
    @Operation(summary = "fcm token 저장")
    @PostMapping("/fcm")
    public void saveFcmToken(
            @AuthenticationPrincipal CustomUserDetails details,
            @RequestBody FcmTokenRequest request
    ) {
       notificationService.saveFcmToken(Long.valueOf(details.getUserId()), request);
    }

    /* ------------------ 소셜 로그인 ---------------*/
    @Operation(summary = "카카오 로그인 콜백")
    @GetMapping("/kakao/callback")
    public LoginResponse kakaoCallback(
            @RequestParam(name = "code", required = false) String code,
            @RequestParam(name = "state", required = false) String state,
            @RequestParam(name = "error", required = false) String error,
            @RequestParam(name = "error_description", required = false) String errorDescription
    ) {
        if(error != null) {
            // 카카오 로그인 실패
            if("access_denied".equals(errorDescription)) {
                throw new BusinessException(SocialLoginErrorCode.KAKAO_UNAUTHORIZED);
            } else {
                throw new BusinessException(SocialLoginErrorCode.KAKAO_FORBIDDEN);
            }
        }

        return authService.kakaoLoginCallback(code);
    }

    @Operation(summary = "네이버 로그인 콜백")
    @GetMapping("/naver/callback")
    public LoginResponse naverCallback(
            @RequestParam(name = "code", required = false) String code,
            @RequestParam(name = "state") String state,
            @RequestParam(name = "error", required = false) String error,
            @RequestParam(name = "error_description", required = false) String errorDescription
    ) {
        if(error != null) {
            // 네이버 로그인 실패
            throw new BusinessException(SocialLoginErrorCode.NAVER_FORBIDDEN);
        }

        return authService.naverLoginCallback(code, state);
    }

    /**
     * 카카오 로그인
     */
    @Operation(summary = "카카오 로그인")
    @PostMapping("/kakao/login")
    public LoginResponse kakaoLogin(
            @RequestBody KakaoLoginRequest request
    ) {
        return authService.kakaoLogin(request);
    }

    /**
     * 네이버 로그인
     */
    @Operation(summary = "네이버 로그인")
    @PostMapping("/naver/login")
    public LoginResponse naverLogin(
            @RequestBody NaverLoginRequest request
    ) {
        return authService.naverLogin(request);
    }

    /**
     * 애플 로그인
     */
    @Operation(summary = "애플 로그인")
    @PostMapping("/apple/login")
    public LoginResponse appleLogin(
            @RequestBody AppleLoginRequest request
    ) {
        return authService.appleLogin(request);
    }
}
