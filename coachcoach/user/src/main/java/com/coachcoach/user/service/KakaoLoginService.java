package com.coachcoach.user.service;

import com.coachcoach.common.exception.BusinessException;
import com.coachcoach.user.dto.response.KakaoAccessTokenValidateResponse;
import com.coachcoach.user.dto.response.KakaoTokenResponse;
import com.coachcoach.user.dto.response.KakaoUserInfoResponse;
import com.coachcoach.user.dto.response.LoginResponse;
import com.coachcoach.user.exception.SocialLoginErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class KakaoLoginService {

    @Value("${social.kakao.client-id}")
    private String KAUTH_CLIENT_ID;

    @Value("${social.kakao.client-secret}")
    private String KAUTH_CLIENT_SECRET;

    @Value("${prod.url}")
    private String PROD_URL;

    @Value("${social.kakao.admin-key}")
    private String KAUTH_ADMIN_KEY;

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

    /**
     * 카카오 Access token 검증
     */
    public KakaoAccessTokenValidateResponse validateAccessToken(String accessToken) {
        return kakaoApiWebClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/v1/user/access_token_info")
                                .build()

                )
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> {
                                    log.error("카카오 접근 토큰 검증 API 에러: {}", body);

                                    return Mono.error(mapKakaoException(body));
                                })
                )
                .bodyToMono(KakaoAccessTokenValidateResponse.class)
                .block();
    }

    /**
     * 카카오 Access token으로 유저 정보 받기
     * @param accessToken
     * @return
     */
    public KakaoUserInfoResponse getSubject(String accessToken) {
        return kakaoApiWebClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/v2/user/me")
                                .build()
                )
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> {
                                    log.error("카카오 회원 정보 조회 API 에러: {}", body);

                                    return Mono.error(mapKakaoException(body));
                                })
                )
                .bodyToMono(KakaoUserInfoResponse.class)
                .block();

    }

    /**
     * unlink
     * @param socialSub
     */
    public void unlink(String socialSub) {
        kakaoApiWebClient.post()
                .uri("/v1/user/unlink")
                .header("Authorization", "KakaoAK " + KAUTH_ADMIN_KEY)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .bodyValue("target_id_type=user_id&target_id=" + socialSub)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(e -> log.error("카카오 unlink 실패: socialSub={}", socialSub, e))
                .onErrorComplete()
                .block();
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
