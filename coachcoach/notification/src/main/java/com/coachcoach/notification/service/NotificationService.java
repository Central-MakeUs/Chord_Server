package com.coachcoach.notification.service;


import com.coachcoach.notification.domain.FcmToken;
import com.coachcoach.notification.dto.request.*;
import com.coachcoach.notification.repository.FcmTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final FcmNotificationService fcmNotificationService;
    private final FcmTokenRepository fcmTokenRepository;

    public void deleteByToken(String fcmToken) {

        if(fcmToken != null) {
            fcmTokenRepository.deleteByToken(fcmToken);
        }
    }
    public void deleteAllByUserId(Long userId) {

        fcmTokenRepository.deleteAllByUserId(userId);
    }

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

    // fcm 토큰 저장
    @Transactional(transactionManager = "transactionManager")
    public void saveFcmToken(Long userId, String fcmToken, String deviceType, String deviceId) {
        List<FcmToken> fcmTokens = fcmTokenRepository.findAllByUserIdAndDeviceTypeAndDeviceId(userId, deviceType, deviceId);

        if(!fcmTokens.isEmpty()) {
            fcmTokenRepository.deleteAll(fcmTokens);
        }

        FcmToken token = fcmTokenRepository.save(
                FcmToken.builder()
                        .userId(userId)
                        .token(fcmToken)
                        .deviceType(deviceType)
                        .deviceId(deviceId)
                        .createdAt(LocalDateTime.now())
                        .build()
        );
    }
}
