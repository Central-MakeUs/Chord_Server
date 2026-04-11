package com.coachcoach.user.service;

import com.coachcoach.common.exception.BusinessException;
import com.coachcoach.user.dto.response.NaverTokenResponse;
import com.coachcoach.user.dto.response.NaverUserInfoResponse;
import com.coachcoach.user.exception.SocialLoginErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class NaverLoginService {

    @Value("${social.naver.client-id}")
    private String NAVER_AUTH_CLIENT_ID;

    @Value("${social.naver.client-secret}")
    private String NAVER_AUTH_CLIENT_SECRET;

    private final WebClient naverAuthWebClient;
    private final WebClient naverApiWebClient;

    public NaverLoginService(
            @Qualifier("naverAuthWebClient") WebClient naverAuthWebClient,
            @Qualifier("naverApiWebClient") WebClient naverApiWebClient
    ) {
        this.naverAuthWebClient = naverAuthWebClient;
        this.naverApiWebClient = naverApiWebClient;
    }

    /**
     * 인가 코드 + state를 입력받아 access token get
     * @param code
     * @param state
     * @return
     */
    public NaverTokenResponse getToken(String code, String state) {
        return naverAuthWebClient.post()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/token")
                                .queryParam("grant_type", "authorization_code")
                                .queryParam("client_id", NAVER_AUTH_CLIENT_ID)
                                .queryParam("client_secret", NAVER_AUTH_CLIENT_SECRET)
                                .queryParam("code", code)
                                .queryParam("state", state)
                                .build()
                )
                .retrieve()
                .bodyToMono(NaverTokenResponse.class)
                .block();
    }

    public NaverUserInfoResponse getSubject(String accessToken) {
        return naverApiWebClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/v1/nid/me")
                                .build()
                )
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                    response.bodyToMono(String.class)
                            .flatMap(body -> {
                                log.error("네이버 회원 정보 조회 API 에러: {}", body);

                                return Mono.error(mapNaverException(body));
                            })
                )
                .bodyToMono(NaverUserInfoResponse.class)
                .block();
    }

    /**
     * unlink
     * @param accessToken
     */
    public void unlink(String accessToken) {
        naverAuthWebClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/token")
                        .queryParam("grant_type", "delete")
                        .queryParam("client_id", NAVER_AUTH_CLIENT_ID)
                        .queryParam("client_secret", NAVER_AUTH_CLIENT_SECRET)
                        .queryParam("access_token", accessToken)
                        .queryParam("service_provider", "NAVER")
                        .build())
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(e -> log.error("네이버 unlink 실패", e))
                .onErrorComplete()
                .block();
    }

    private BusinessException mapNaverException(String body) {
        if(body.contains("\"errorCode\":\"024\"")) {
            return new BusinessException(SocialLoginErrorCode.NAVER_UNAUTHORIZED);
        } else if(body.contains("\"errorCode\":\"028\"")) {
            return new BusinessException(SocialLoginErrorCode.NAVER_UNAUTHORIZED);
        } else if(body.contains("\"errorCode\":\"403\"")) {
            return new BusinessException(SocialLoginErrorCode.NAVER_FORBIDDEN);
        } else if(body.contains("\"errorCode\":\"404\"")) {
            return new BusinessException(SocialLoginErrorCode.NAVER_NOT_FOUND);
        } else if(body.contains("\"errorCode\":\"500\"")){
            return new BusinessException(SocialLoginErrorCode.NAVER_SERVER_ERROR);
        }

        return new BusinessException(SocialLoginErrorCode.NAVER_FORBIDDEN);
    }
}
