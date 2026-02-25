package com.coachcoach.common.api;

public interface NotificationQueryApi {
    void sendEach(Long userId, String title, String body);
    void sendAll(String title, String body);
}
