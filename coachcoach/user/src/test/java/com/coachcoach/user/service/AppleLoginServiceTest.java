package com.coachcoach.user.service;

import com.coachcoach.common.exception.BusinessException;
import com.coachcoach.user.dto.response.ApplePublicKeyResponse;
import com.coachcoach.user.exception.SocialLoginErrorCode;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Date;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class AppleLoginServiceTest {

    @InjectMocks
    private AppleLoginService appleLoginService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(appleLoginService, "CLIENT_ID", "com.seungwan.coachcoach");
    }

    /**
     * extractKidFromToken (JWT 헤더에서 공개키 추출 검증)
     */
    @Test
    void should_extract_kid_from_jwt_header() {
        // given
        String header = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"alg\":\"RS256\",\"kid\":\"ABC123\"}".getBytes());
        String fakeToken = header + ".payload.signature";

        // when
        String kid = appleLoginService.extractKidFromToken(fakeToken);

        // then
        assertThat(kid).isEqualTo("ABC123");
    }

    @Test
    void should_throw_APPLE_INVALID_TOKEN_when_jwt_format_is_invalid() {
        // given
        String invalidToken = "invalid-token";

        // when & then
        assertThatThrownBy(() -> appleLoginService.extractKidFromToken(invalidToken))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", SocialLoginErrorCode.APPLE_INVALID_TOKEN);
    }

    @Test
    void should_throw_APPLE_INVALID_TOKEN_when_kid_field_is_missing() {
        // given
        String header = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"alg\":\"RS256\"}".getBytes());
        String fakeToken = header + ".payload.signature";

        // when & then
        assertThatThrownBy(() -> appleLoginService.extractKidFromToken(fakeToken))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", SocialLoginErrorCode.APPLE_INVALID_TOKEN);
    }

    /**
     *  buildPublicKey (RSA 공개키 객체 생성 로직 검증)
     */
    @Test
    void should_build_rsa_public_key_with_valid_n_and_e() throws NoSuchAlgorithmException {
        // given - 실제 RSA 키쌍 생성해서 n, e 추출
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();

        String n = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(rsaPublicKey.getModulus().toByteArray());
        String e = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(rsaPublicKey.getPublicExponent().toByteArray());

        ApplePublicKeyResponse.ApplePublicKey key = mock(ApplePublicKeyResponse.ApplePublicKey.class);
        given(key.getN()).willReturn(n);
        given(key.getE()).willReturn(e);

        // when & then
        assertThatNoException()
                .isThrownBy(() -> appleLoginService.buildPublicKey(key));
    }

    @Test
    void should_throw_APPLE_PUBLIC_KEY_INVALID_when_n_is_invalid() {
        // given
        ApplePublicKeyResponse.ApplePublicKey key = mock(ApplePublicKeyResponse.ApplePublicKey.class);
        given(key.getN()).willReturn("!@#$%^&*");

        // when & then
        assertThatThrownBy(() -> appleLoginService.buildPublicKey(key))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", SocialLoginErrorCode.APPLE_PUBLIC_KEY_INVALID);
    }

    @Test
    void should_throw_APPLE_PUBLIC_KEY_INVALID_when_e_is_invalid() {
        // given
        ApplePublicKeyResponse.ApplePublicKey key = mock(ApplePublicKeyResponse.ApplePublicKey.class);
        given(key.getN()).willReturn("valid_n");
        given(key.getE()).willReturn("invalid_e!!!");

        // when & then
        assertThatThrownBy(() -> appleLoginService.buildPublicKey(key))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", SocialLoginErrorCode.APPLE_PUBLIC_KEY_INVALID);
    }

    /**
     * validClaims
     */
    @Test
    void should_pass_validation_when_claims_are_valid() {
        // given
        Claims claims = mock(Claims.class);
        given(claims.getIssuer()).willReturn("https://appleid.apple.com");
        given(claims.getAudience()).willReturn(Set.of("com.seungwan.coachcoach"));
        given(claims.getExpiration()).willReturn(new Date(System.currentTimeMillis() + 10000));

        // when & then
        assertThatNoException()
                .isThrownBy(() -> appleLoginService.validateClaims(claims));
    }

    @Test
    void should_throw_APPLE_INVALID_ISSUER_when_iss_is_wrong() {
        // given
        Claims claims = mock(Claims.class);
        given(claims.getIssuer()).willReturn("https://invalid.com");

        // when & then
        assertThatThrownBy(() -> appleLoginService.validateClaims(claims))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", SocialLoginErrorCode.APPLE_INVALID_ISSUER);
    }

    @Test
    void should_throw_APPLE_INVALID_AUDIENCE_when_client_id_not_in_aud() {
        // given
        Claims claims = mock(Claims.class);
        given(claims.getIssuer()).willReturn("https://appleid.apple.com");
        given(claims.getAudience()).willReturn(Set.of("com.wrong.app"));

        // when & then
        assertThatThrownBy(() -> appleLoginService.validateClaims(claims))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", SocialLoginErrorCode.APPLE_INVALID_AUDIENCE);
    }

    @Test
    void should_throw_APPLE_EXPIRED_TOKEN_when_token_is_expired() {
        // given
        Claims claims = mock(Claims.class);
        given(claims.getIssuer()).willReturn("https://appleid.apple.com");
        given(claims.getAudience()).willReturn(Set.of("com.seungwan.coachcoach"));
        given(claims.getExpiration()).willReturn(new Date(System.currentTimeMillis() - 10000)); // 과거

        // when & then
        assertThatThrownBy(() -> appleLoginService.validateClaims(claims))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", SocialLoginErrorCode.APPLE_EXPIRED_TOKEN);
    }
}
