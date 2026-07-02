package com.coachcoach.notification.api;

import com.coachcoach.common.api.NotificationQueryApi;
import com.coachcoach.notification.service.NotificationService;
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

    @Override
    public void sendAll(String title, String body) { notificationService.sendAll(title, body); }

    @Override
    public void deleteByToken(String fcmToken) {
        notificationService.deleteByToken(fcmToken);
    }

    @Override
    public void deleteAllByUserId(Long userId) {
        notificationService.deleteAllByUserId(userId);
    }

    @Override
    public void saveFcmToken(Long userId, String fcmToken, String deviceType, String deviceId) {
        notificationService.saveFcmToken(userId, fcmToken, deviceType, deviceId);
    }
}
