package com.coachcoach.user.service;

import com.coachcoach.common.dto.notification.MulticastNotificationRequest;
import com.coachcoach.common.dto.notification.SingleNotificationRequest;
import com.coachcoach.common.exception.BusinessException;
import com.coachcoach.common.exception.NotificationErrorCode;
import com.coachcoach.common.notification.FcmNotificationService;
import com.coachcoach.user.domain.FcmToken;
import com.coachcoach.user.dto.request.NotificationContentRequest;
import com.coachcoach.user.dto.request.NotificationTokenRequest;
import com.coachcoach.user.repository.FcmTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final FcmNotificationService fcmNotificationService;
    private final FcmTokenRepository fcmTokenRepository;

    /**
     * 모든 유저 알림 일괄 전송
     */
    public void sendAll(
            NotificationContentRequest request
    ) {
        List<FcmToken> all = fcmTokenRepository.findAll();
        List<String> tokens = all.stream().map(FcmToken::getToken).toList();

        if(tokens.isEmpty())
            return;

        MulticastNotificationRequest multicastNotificationRequest = MulticastNotificationRequest.of(tokens, request.title(), request.body());
        fcmNotificationService.sendMessage(multicastNotificationRequest);
    }

    public void sendAll(
            String title,
            String body
    ) {
        List<FcmToken> all = fcmTokenRepository.findAll();
        List<String> tokens = all.stream().map(FcmToken::getToken).toList();

        if(tokens.isEmpty())
            return;

        MulticastNotificationRequest multicastNotificationRequest = MulticastNotificationRequest.of(tokens, title, body);
        fcmNotificationService.sendMessage(multicastNotificationRequest);
    }


    /**
     * 개별 유저 알림 전송 (토큰)
     */
    public void sendEachWithToken(
            Long userId,
            NotificationTokenRequest request
    ) {
        SingleNotificationRequest singleNotificationRequest = SingleNotificationRequest.of(request.token(), request.title(), request.body());
        fcmNotificationService.sendMessage(singleNotificationRequest);
    }

    public void sendEachWithToken(
            Long userId,
            String token,
            String title,
            String body
    ) {
        SingleNotificationRequest singleNotificationRequest = SingleNotificationRequest.of(token, title, body);
        fcmNotificationService.sendMessage(singleNotificationRequest);
    }


    /**
     * 개별 유저 알림 전송
     */
    public void sendEach(
            Long userId,
            NotificationContentRequest request
    ) {
        FcmToken token = fcmTokenRepository.findByUserId(userId)
                .orElse(null);

        if(token == null)
            return;

        SingleNotificationRequest singleNotificationRequest = SingleNotificationRequest.of(token.getToken(), request.title(), request.body());
        fcmNotificationService.sendMessage(singleNotificationRequest);

    }

    public void sendEach(
            Long userId,
            String title,
            String body
    ) {
        FcmToken token = fcmTokenRepository.findByUserId(userId)
                .orElse(null);

        if(token == null)
            return;

        SingleNotificationRequest singleNotificationRequest = SingleNotificationRequest.of(token.getToken(), title, body);
        fcmNotificationService.sendMessage(singleNotificationRequest);

    }

}
