package com.coachcoach.user.service;

import com.coachcoach.common.dto.notification.MulticastNotificationRequest;
import com.coachcoach.common.dto.notification.SingleNotificationRequest;
import com.coachcoach.common.exception.BusinessException;
import com.coachcoach.common.exception.NotificationErrorCode;
import com.coachcoach.common.notification.FcmNotificationService;
import com.coachcoach.user.domain.FcmToken;
import com.coachcoach.user.dto.request.FcmTokenRequest;
import com.coachcoach.user.dto.request.NotificationContentRequest;
import com.coachcoach.user.dto.request.NotificationTokenRequest;
import com.coachcoach.user.repository.FcmTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
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

    // fcm 토큰 저장
    @Transactional(transactionManager = "transactionManager")
    public void saveFcmToken(Long userId, FcmTokenRequest request) {
        List<FcmToken> fcmTokens = fcmTokenRepository.findAllByUserIdAndDeviceTypeAndDeviceId(userId, request.deviceType(), request.deviceId());

        if(!fcmTokens.isEmpty()) {
            fcmTokenRepository.deleteAll(fcmTokens);
        }

        FcmToken fcmToken = fcmTokenRepository.save(
                FcmToken.builder()
                        .userId(userId)
                        .token(request.fcmToken())
                        .deviceType(request.deviceType())
                        .deviceId(request.deviceId())
                        .createdAt(LocalDateTime.now())
                        .build()
        );
    }
}
