package com.coachcoach.user.service;

import com.coachcoach.user.dto.response.KakaoTokenResponse;
import com.coachcoach.user.dto.response.KakaoUserInfoResponse;
import com.coachcoach.user.dto.response.LoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class KakaoLoginService {

    @Value("${social.kakao.client-id}")
    private String KAUTH_CLIENT_ID;

    @Value("${social.kakao.client-secret}")
    private String KAUTH_CLIENT_SECRET;

    @Value("${prod.url}")
    private String PROD_URL;

    private final WebClient kakaoAuthWebClient;
    private final WebClient kakaoApiWebClient;

    public KakaoLoginService(
            @Qualifier("kakaoAuthWebClient") WebClient kakaoAuthWebClient,
            @Qualifier("kakaoApiWebClient") WebClient kakaoApiWebClient
    ) {
        this.kakaoAuthWebClient = kakaoAuthWebClient;
        this.kakaoApiWebClient = kakaoApiWebClient;
    }

    /**
     * 인가 코드를 입력으로 받아 access token get
     * @param code
     * @return
     */
    public KakaoTokenResponse getToken(String code) {

        return kakaoAuthWebClient.post()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/token")
                                .queryParam("grant_type", "authorization_code")
                                .queryParam("client_id", KAUTH_CLIENT_ID)
                                .queryParam("redirect_uri", PROD_URL + "/api/v1/auth/kakao/callback")
                                .queryParam("code", code)
                                .queryParam("client_secret", KAUTH_CLIENT_SECRET)
                                .build()
                )
                .retrieve()
                .bodyToMono(KakaoTokenResponse.class)
                .block();
    }

    public KakaoUserInfoResponse getSubject(String accessToken) {
        return kakaoApiWebClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/v2/user/me")
                                .build()
                )
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(KakaoUserInfoResponse.class)
                .block();

    }
}
