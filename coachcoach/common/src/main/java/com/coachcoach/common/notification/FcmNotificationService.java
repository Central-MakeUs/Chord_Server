package com.coachcoach.common.notification;

import com.coachcoach.common.dto.notification.MulticastNotificationRequest;
import com.coachcoach.common.dto.notification.NotificationRequest;
import com.coachcoach.common.dto.notification.SingleNotificationRequest;
import com.coachcoach.common.dto.notification.TopicNotificationRequest;
import com.coachcoach.common.exception.BusinessException;
import com.coachcoach.common.exception.NotificationErrorCode;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.ApsAlert;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FcmNotificationService {

    private final FirebaseMessaging firebaseMessaging;

    public void sendMessage(final SingleNotificationRequest request) {
        val message = request.buildSendMessage().setApnsConfig(getApnsConfig(request)).build();
        firebaseMessaging.sendAsync(message);
    }

    public void sendMessage(final TopicNotificationRequest request) {
        val message = request.buildSendMessage().setApnsConfig(getApnsConfig(request)).build();
        firebaseMessaging.sendAsync(message);
    }

    public void sendMessage(final MulticastNotificationRequest request) {
        val message = request.buildSendMessage().setApnsConfig(getApnsConfig(request)).build();
        firebaseMessaging.sendEachForMulticastAsync(message);
    }

    private ApnsConfig getApnsConfig(NotificationRequest request) {
        val alert = ApsAlert.builder().setTitle(request.title()).setBody(request.body()).build();
        val aps = Aps.builder().setAlert(alert).setSound("default").build();
        return ApnsConfig.builder().setAps(aps).build();
    }
}
