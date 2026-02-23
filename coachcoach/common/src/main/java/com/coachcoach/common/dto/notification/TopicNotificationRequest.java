package com.coachcoach.common.dto.notification;

import com.google.firebase.messaging.Message;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record TopicNotificationRequest(
        String topic,
        String title,
        String body
) implements NotificationRequest{
    public TopicNotificationRequest of(String topic, String title, String body) {
        return TopicNotificationRequest.builder()
                .topic(topic)
                .title(title)
                .body(body)
                .build();
    }

    public Message.Builder buildSendMessage() {
        return Message.builder()
                .setTopic(topic)
                .setNotification(notification());
    }
}
