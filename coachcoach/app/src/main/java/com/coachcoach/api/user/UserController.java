package com.coachcoach.api.user;

import com.coachcoach.user.dto.request.LoginRequest;
import com.coachcoach.user.dto.request.SignUpRequest;
import com.coachcoach.user.dto.response.LoginResponse;
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

@Tag(name = "유저", description = "유저 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {

    private final AuthService authService;
    private final UserService userService;

    /**
     * 회원가입
     */
    @Operation(summary = "회원가입")
    @PostMapping("/auth/sign-up")
    public void signUp(
            @Valid @RequestBody SignUpRequest request
    ) {
        authService.signUp(request);
    }

    /**
     * 로그인
     */
    @Operation(summary = "로그인")
    @PostMapping("/auth/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request
    ) {
        return authService.login(request);
    }

    /**
     * 온보딩
     */
}
