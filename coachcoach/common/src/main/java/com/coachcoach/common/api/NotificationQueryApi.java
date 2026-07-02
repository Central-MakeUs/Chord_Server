package com.coachcoach.common.api;

public interface NotificationQueryApi {
    void sendEach(Long userId, String title, String body);
    void sendAll(String title, String body);
    void deleteByToken(String fcmToken);
    void deleteAllByUserId(Long userId);
    void saveFcmToken(Long userId, String fcmToken, String deviceType, String deviceId);
}
