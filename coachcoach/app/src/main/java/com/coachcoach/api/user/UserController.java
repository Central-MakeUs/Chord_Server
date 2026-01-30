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
import org.springframework.web.bind.annotation.*;

@Tag(name = "유저", description = "유저 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;
}
