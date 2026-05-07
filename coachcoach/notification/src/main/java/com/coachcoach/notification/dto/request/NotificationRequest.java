package com.coachcoach.notification.dto.request;

import com.google.firebase.messaging.Notification;

public interface NotificationRequest {
    String title();
    String body();

    default Notification notification() {
        return Notification.builder()
                .setTitle(title())
                .setBody(body())
                .build();
    }
}

