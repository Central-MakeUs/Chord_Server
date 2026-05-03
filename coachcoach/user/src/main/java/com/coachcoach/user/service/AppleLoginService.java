package com.coachcoach.user.service;

import com.coachcoach.common.exception.BusinessException;
import com.coachcoach.user.dto.apple.AppleUserInfo;
import com.coachcoach.user.dto.response.ApplePublicKeyResponse;
import com.coachcoach.user.dto.response.AppleTokenResponse;
import com.coachcoach.user.exception.SocialLoginErrorCode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Date;

@Service
@Slf4j
public class AppleLoginService {

    @Value("${social.apple.team-id}")
    private String TEAM_ID;

    @Value("${social.apple.client-id}")
    private String CLIENT_ID;

    @Value("${social.apple.key-id}")
    private String KEY_ID;

    @Value("${social.apple.private-key}")
    private String PRIVATE_KEY;

    private final WebClient appleAuthWebClient;

    public AppleLoginService(
            @Qualifier("appleAuthWebClient") WebClient appleAuthWebClient
    ) {
        this.appleAuthWebClient = appleAuthWebClient;
    }

    /**
     * Apple 공개 키 목록 조회
     */
    public ApplePublicKeyResponse getApplePublicKeys() {
        return appleAuthWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/keys")
                        .build()
                )
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> {
                                    log.error("애플 공개 키 목록 조회 API 에러: {}", body);

                                    return Mono.error(new BusinessException(SocialLoginErrorCode.APPLE_PUBLIC_KEY_FETCH_FAILED));
                                }))
                .bodyToMono(ApplePublicKeyResponse.class)
                .block();
    }

    /**
     * identity token 검증 & 사용자 정보 추출
     */
    public AppleUserInfo verifyAndExtract(String identityToken) {
        String kid = extractKidFromToken(identityToken);

        // 공개키 목록 조회
        ApplePublicKeyResponse publicKeys = getApplePublicKeys();

        // 공개키 검증
        ApplePublicKeyResponse.ApplePublicKey matchedKey = publicKeys.keys().stream()
                .filter(key -> key.getKid().equals(kid))
                .findFirst()
                .orElseThrow(() -> new BusinessException(SocialLoginErrorCode.APPLE_PUBLIC_KEY_NOT_FOUND));


        // 공개키 객체 생성
        PublicKey publicKey = buildPublicKey(matchedKey);

        // Claims 추출
        Claims claims = parseClaimsOrThrow(identityToken, publicKey);

        // Claims 검증
        validateClaims(claims);

        return new AppleUserInfo(claims.getSubject());

    }


    /**
     * JWT 헤더에서 kid(서명한 공개키) 추출
     */
    public String extractKidFromToken(String token) {
        try {
            String headerBase64 = token.split("\\.")[0];
            String headerJson = new String(Base64.getUrlDecoder().decode(headerBase64));
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode header = objectMapper.readTree(headerJson);
            return header.get("kid").asText();
        } catch (Exception e) {
            throw new BusinessException(SocialLoginErrorCode.APPLE_INVALID_TOKEN);
        }
    }

    /**
     * RSA PublicKey 객체 생성
     */
    public PublicKey buildPublicKey(ApplePublicKeyResponse.ApplePublicKey key) {
        try {
            byte[] nBytes = Base64.getUrlDecoder().decode(key.getN());
            byte[] eBytes = Base64.getUrlDecoder().decode(key.getE());

            RSAPublicKeySpec spec = new RSAPublicKeySpec(
                    new BigInteger(1, nBytes),
                    new BigInteger(1, eBytes)
            );
            return KeyFactory.getInstance("RSA").generatePublic(spec);
        } catch (Exception e) {
            throw new BusinessException(SocialLoginErrorCode.APPLE_PUBLIC_KEY_INVALID);
        }
    }

    /**
     * 공개키 서명 검증 & Claims 파싱
     */
    private Claims parseClaimsOrThrow(String identityToken, PublicKey publicKey) {
        try {
            return Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(identityToken)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new BusinessException(SocialLoginErrorCode.APPLE_EXPIRED_TOKEN);
        } catch (JwtException e) {
            throw new BusinessException(SocialLoginErrorCode.APPLE_INVALID_TOKEN);
        }
    }

    /**
     * Claims 검증
     */
    public void validateClaims(Claims claims) {
        if(!"https://appleid.apple.com".equals(claims.getIssuer())) {
            throw new BusinessException(SocialLoginErrorCode.APPLE_INVALID_ISSUER);
        }

        if(!claims.getAudience().contains(CLIENT_ID)) {
            throw new BusinessException(SocialLoginErrorCode.APPLE_INVALID_AUDIENCE);
        }

        if(claims.getExpiration().before(new Date())) {
            throw new BusinessException(SocialLoginErrorCode.APPLE_EXPIRED_TOKEN);
        }
    }

    /**
     * 토큰 무효화
     */
    public void revoke(String authorizationCode) {
        String accessToken = getAccessToken(authorizationCode);
        revokeToken(accessToken);
    }

    /**
     * revoke token
     */
    private void revokeToken(String accessToken) {
        appleAuthWebClient.post()
                .uri(uriBuilder -> uriBuilder.path("/revoke")
                        .build()
                )
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("client_id=" + CLIENT_ID + "&client_secret=" + generateClientSecret() + "&token=" + accessToken + "&token_type_hint=access_token")
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class)
                        .flatMap(body -> {
                            log.error("애플 revoke API 에러: {}", body);

                            return Mono.error(new BusinessException(SocialLoginErrorCode.APPLE_UNAUTHORIZED));
                        })
                )
                .bodyToMono(Void.class)
                .block();
    }

    /**
     * access token 발급
     */
    private String getAccessToken(String authorizationCode) {
        AppleTokenResponse res = appleAuthWebClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/token")
                        .build()
                )
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("client_id=" + CLIENT_ID + "&client_secret=" + generateClientSecret() + "&code=" + authorizationCode + "&grant_type=authorization_code")
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> {
                                    log.error("애플 Access Token 발급 API 에러: {}", body);

                                    return Mono.error(new BusinessException(SocialLoginErrorCode.APPLE_UNAUTHORIZED));
                                })
                )
                .bodyToMono(AppleTokenResponse.class)
                .block();

        return res.accessToken();
    }

    /**
     * client secret JWT 생성
     */
    private String generateClientSecret() {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(PRIVATE_KEY);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            PrivateKey signingKey = KeyFactory.getInstance("EC").generatePrivate(keySpec);

            return Jwts.builder()
                    .header().add("kid", KEY_ID).and()
                    .issuer(TEAM_ID)
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 5)) // 5분
                    .audience().add("https://appleid.apple.com").and()
                    .subject(CLIENT_ID)
                    .signWith(signingKey)
                    .compact();
        } catch (Exception e) {
            throw new BusinessException(SocialLoginErrorCode.APPLE_UNAUTHORIZED);
        }
    }
}
