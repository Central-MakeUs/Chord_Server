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

    @Value("${social.kakao.api-url}")
    private String KAKAO_API_URL;

    @Value("${social.naver.api-url}")
    private String NAVER_API_URL;

    @Bean
    public WebClient kakaoAuthWebClient() {
        return WebClient.builder()
                .baseUrl(KAKAO_AUTH_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .filter((request, next) ->
                        next.exchange(request)
                                .flatMap(response -> {
                                    if (response.statusCode().isError()) {
                                        return response.bodyToMono(String.class)
                                                .flatMap(body -> {
                                                    log.error("카카오 API 에러: {}", body);

                                                    return Mono.error(mapKakaoException(body));
                                                });
                                    }
                                    return Mono.just(response);
                                })
                )
                .build();
    }

    @Bean WebClient kakaoApiWebClient() {
        return WebClient.builder()
                .baseUrl(KAKAO_API_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .filter((request, next) ->
                        next.exchange(request)
                                .flatMap(response -> {
                                    if (response.statusCode().isError()) {
                                        return response.bodyToMono(String.class)
                                                .flatMap(body -> {
                                                    log.error("카카오 API 에러: {}", body);

                                                    return Mono.error(mapKakaoException(body));
                                                });
                                    }
                                    return Mono.just(response);
                                })
                )
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

    private BusinessException mapKakaoException(String body) {
        if (body.contains("\"code\":-101")) {
            return new BusinessException(SocialLoginErrorCode.KAKAO_NOT_LINKED);
        } else if (body.contains("\"code\":-102")) {
            return new BusinessException(SocialLoginErrorCode.KAKAO_ALREADY_LINKED);
        } else if (body.contains("\"code\":-103")) {
            return new BusinessException(SocialLoginErrorCode.KAKAO_INVALID_USER);
        } else if (body.contains("\"code\":-201")) {
            return new BusinessException(SocialLoginErrorCode.KAKAO_INVALID_PROPERTY);
        } else if (body.contains("\"code\":-402")) {
            return new BusinessException(SocialLoginErrorCode.KAKAO_FORBIDDEN);
        } else if (body.contains("\"code\":-406")) {
            return new BusinessException(SocialLoginErrorCode.KAKAO_UNAUTHORIZED);
        }

        return new BusinessException(SocialLoginErrorCode.KAKAO_BAD_REQUEST);
    }
}
