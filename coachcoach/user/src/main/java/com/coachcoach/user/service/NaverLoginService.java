package com.coachcoach.user.service;

import com.coachcoach.user.dto.response.NaverTokenResponse;
import com.coachcoach.user.dto.response.NaverUserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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
                                .scheme("https")
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
                                .scheme("https")
                                .path("/v1/nid/me")
                                .build()
                )
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(NaverUserInfoResponse.class)
                .block();
    }
}
