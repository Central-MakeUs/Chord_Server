package com.coachcoach.app.api.notification;

import com.coachcoach.common.dto.notification.MulticastNotificationRequest;
import com.coachcoach.common.notification.FcmNotificationService;
import com.coachcoach.user.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "알림", description = "알림 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "모든 유저 일괄 알림 전송")
    @PostMapping("/all")
    public void sendAll(
            @RequestBody MulticastNotificationRequest request
    ) {

    }

    @Operation(summary = "개별 유저 알림 전송 (토큰)")
    @PostMapping("/{userId}")
    public void sendEachWithToken(
            @PathVariable Long userId
    ) {

    }

    @Operation(summary = "개별 유저 알림 전송")
    @PostMapping("/{userId}")
    public void sendEach(
            @PathVariable Long userId
    ) {

    }

}
