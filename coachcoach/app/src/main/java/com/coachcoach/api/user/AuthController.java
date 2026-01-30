package com.coachcoach.api.user;

import com.coachcoach.user.dto.request.LoginRequest;
import com.coachcoach.user.dto.request.SignUpRequest;
import com.coachcoach.user.dto.request.TokenRefreshRequest;
import com.coachcoach.user.dto.response.LoginResponse;
import com.coachcoach.user.dto.response.TokenRefreshResponse;
import com.coachcoach.user.service.AuthService;
import com.coachcoach.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "인증/인가", description = "인증/인가 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

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
}
