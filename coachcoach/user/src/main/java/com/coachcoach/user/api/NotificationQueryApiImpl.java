package com.coachcoach.user.api;

import com.coachcoach.common.api.NotificationQueryApi;
import com.coachcoach.user.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationQueryApiImpl implements NotificationQueryApi {

    private final NotificationService notificationService;

    @Override
    public void sendEach(Long userId, String title, String body) {
        notificationService.sendEach(userId, title, body);
    }
}
