package com.coachcoach.common.dto.notification;

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

