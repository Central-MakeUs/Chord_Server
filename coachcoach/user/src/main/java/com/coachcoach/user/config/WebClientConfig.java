package com.coachcoach.user.config;

import com.coachcoach.common.exception.BusinessException;
import com.coachcoach.user.exception.SocialLoginErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class WebClientConfig {

    @Value("${social.kakao.auth-url}")
    private String KAKAO_AUTH_URL;

    @Value("${social.naver.auth-url}")
    private String NAVER_AUTH_URL;

    @Value("${social.apple.auth-url}")
    private String APPLE_AUTH_URL;

    @Value("${social.kakao.api-url}")
    private String KAKAO_API_URL;

    @Value("${social.naver.api-url}")
    private String NAVER_API_URL;


    @Bean
    public WebClient kakaoAuthWebClient() {
        return WebClient.builder()
                .baseUrl(KAKAO_AUTH_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
    }

    @Bean WebClient kakaoApiWebClient() {
        return WebClient.builder()
                .baseUrl(KAKAO_API_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
    }
    @Bean
    public WebClient naverAuthWebClient() {
        return WebClient.builder()
                .baseUrl(NAVER_AUTH_URL)
                .build();
    }

    @Bean
    public WebClient naverApiWebClient() {
        return WebClient.builder()
                .baseUrl(NAVER_API_URL)
                .build();
    }

    @Bean
    public WebClient appleAuthWebClient() {
        return WebClient.builder()
                .baseUrl(APPLE_AUTH_URL)
                .build();
    }
}
