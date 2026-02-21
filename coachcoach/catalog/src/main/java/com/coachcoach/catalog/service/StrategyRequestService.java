package com.coachcoach.catalog.service;

import com.coachcoach.common.api.NotificationQueryApi;
import com.coachcoach.common.dto.notification.NotificationRequest;
import com.coachcoach.common.notification.FcmNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class StrategyRequestService {

    private final RestTemplate restTemplate;

    @Value("${PRIVATE_SERVER_URL}")
    private String privateServerUrl;

    private final NotificationQueryApi notificationQueryApi;

    // 비동기 / 결과 버림
    public void requestDangerStrategy(Long userId, Long menuId) {
        CompletableFuture.runAsync(() -> {
            try {
                restTemplate.exchange(
                        privateServerUrl + "/insight/danger?user_id=" + userId + "&menu_id=" + menuId,
                        HttpMethod.POST,
                        null,
                        Void.class
                );
                log.info("위험 전략 생성 요청 성공 | userId={} | menuId={}", userId, menuId);

                try {
                    notificationQueryApi.sendEach(
                            userId,
                            "전략 카드 생성 완료",
                            "위험 메뉴에 대한 전략 카드가 생성되었어요!"
                    );
                } catch (Exception e) {
                    log.warn("알림 전송 실패 | userId={} | error={}", userId, e.getMessage());
                }

            } catch (Exception e) {
                log.error("위험 전략 생성 요청 실패 | userId={} | menuId={} | error={}", userId, menuId, e.getMessage());
            }
        });
    }
}