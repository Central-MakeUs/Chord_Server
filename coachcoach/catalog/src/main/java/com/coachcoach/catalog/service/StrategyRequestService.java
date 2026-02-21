package com.coachcoach.catalog.service;

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
            } catch (Exception e) {
                log.error("위험 전략 생성 요청 실패 | userId={} | menuId={} | error={}", userId, menuId, e.getMessage());
            }
        });
    }
}