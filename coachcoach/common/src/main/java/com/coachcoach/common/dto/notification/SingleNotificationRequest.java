package com.coachcoach.common.dto.notification;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.Builder;
import lombok.NonNull;

import static lombok.AccessLevel.PRIVATE;

@Builder(access = PRIVATE)
public record SingleNotificationRequest(
        @NonNull String targetToken,
        String title,
        String body
) implements NotificationRequest {
    public static SingleNotificationRequest of(String token, String title, String body) {
        return SingleNotificationRequest.builder()
                .targetToken(token)
                .title(title)
                .body(body)
                .build();
    }

    public Message.Builder buildSendMessage() {
        return Message.builder()
                .setToken(targetToken)
                .setNotification(notification());
    }

}
