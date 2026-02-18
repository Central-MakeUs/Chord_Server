package com.coachcoach.scheduler;

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

            log.info("{} 전략 생성 성공", LocalDateTime.now());
        } catch (Exception e) {
            log.error("{} 전략 생성 실패: {}", LocalDateTime.now(), e.getMessage());
        }
    }
}
