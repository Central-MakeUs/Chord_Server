package com.coachcoach.common.dto.notification;

import com.google.firebase.messaging.MulticastMessage;
import lombok.AccessLevel;
import lombok.Builder;

import java.util.List;

@Builder(access = AccessLevel.PRIVATE)
public record MulticastNotificationRequest(
        List<String> targetTokens,
        String title,
        String body
) implements NotificationRequest {
    public static MulticastNotificationRequest of(List<String> tokens, String title, String body) {
        return MulticastNotificationRequest.builder()
                .targetTokens(tokens)
                .title(title)
                .body(body)
                .build();
    }

    public MulticastMessage.Builder buildSendMessage() {
        return MulticastMessage.builder()
                .setNotification(notification())
                .addAllTokens(targetTokens);
    }
}
