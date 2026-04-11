package com.coachcoach.app.api.user;

import com.coachcoach.common.security.userdetails.CustomUserDetails;
import com.coachcoach.user.dto.request.DeleteUserRequest;
import com.coachcoach.user.dto.request.OnboardingRequest;
import com.coachcoach.user.dto.request.UpdateStoreRequest;
import com.coachcoach.user.dto.response.StoreResponse;
import com.coachcoach.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.Delete;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "유저 V2", description = "유저 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserControllerV2 {
    private final UserService userService;

    /**
     * 회원 탈퇴
     */
    @Operation(summary = "회원 탈퇴")
    @PostMapping("/me")
    public void deleteUser(
            @AuthenticationPrincipal CustomUserDetails details,
            @RequestBody DeleteUserRequest request
            ) {
       userService.deleteUserWithSocial(Long.valueOf(details.getUserId()), request);
    }
}
