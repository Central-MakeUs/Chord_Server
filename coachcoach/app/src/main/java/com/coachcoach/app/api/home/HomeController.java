package com.coachcoach.app.api.home;

import com.coachcoach.catalog.dto.response.HomeMenusResponse;
import com.coachcoach.catalog.service.MenuService;
import com.coachcoach.common.security.userdetails.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "홈화면", description = "홈화면 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/home")
public class HomeController {

    private final MenuService menuService;

    @Operation(summary = "홈화면 메뉴 종합 정보")
    @GetMapping("/menus")
    public HomeMenusResponse getHomeMenus(
            @AuthenticationPrincipal CustomUserDetails details
    ) {
        return menuService.getHomeMenus(Long.valueOf(details.getUserId()));
    }

}
