package com.coachcoach.scheduler;

import com.coachcoach.common.api.NotificationQueryApi;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class Scheduler {

    private final RestTemplate restTemplate;

    private final NotificationQueryApi notificationQueryApi;

    @Value("${PRIVATE_SERVER_URL}")
    private String privateServerUrl;


    @Scheduled(cron = "0 0 22 * * Sun")
    public void generateInsightScheduler() {
        try {
            restTemplate.exchange(
                    privateServerUrl + "/insights",
                    HttpMethod.POST,
                    null,
                    Void.class
            );

            notificationQueryApi.sendAll(
                    "이번 주 추천 전략 업데이트",
                    "새로운 수익 전략이 생성되었어요. 이번 주 전략 가이드를 확인해보세요."
            );
            
            log.info("{} 전략 생성 성공", LocalDateTime.now());
        } catch (Exception e) {
            log.error("{} 전략 생성 실패: {}", LocalDateTime.now(), e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 22 * * Wed")
    public void checkInsightScheduler() {
        try {
            notificationQueryApi.sendAll(
                    "이번 주 전략 가이드 점검",
                    "이번 주 전략 실행 여부를 확인하고 수익 구조를 개선해보세요."
            );
            log.info("{} 알림 전송 성공", LocalDateTime.now());
        }  catch (Exception e) {
            log.error("{} 알림 전송 실패: {}", LocalDateTime.now(), e.getMessage());
        }
    }

}
