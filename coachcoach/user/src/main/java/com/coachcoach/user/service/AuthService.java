package com.coachcoach.user.service;

import com.coachcoach.common.exception.BusinessException;
import com.coachcoach.common.exception.CommonErrorCode;
import com.coachcoach.common.security.jwt.JwtUtil;
import com.coachcoach.user.domain.FcmToken;
import com.coachcoach.user.dto.apple.AppleUserInfo;
import com.coachcoach.user.dto.request.*;
import com.coachcoach.user.dto.response.*;
import com.coachcoach.user.domain.RefreshToken;
import com.coachcoach.user.domain.Store;
import com.coachcoach.user.domain.Users;
import com.coachcoach.user.exception.SocialLoginErrorCode;
import com.coachcoach.user.repository.FcmTokenRepository;
import com.coachcoach.user.repository.RefreshTokenRepository;
import com.coachcoach.user.repository.StoreRepository;
import com.coachcoach.user.repository.UsersRepository;
import com.coachcoach.user.exception.UserErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsersRepository usersRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final StoreRepository storeRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final KakaoLoginService kakaoLoginService;
    private final NaverLoginService naverLoginService;
    private final AppleLoginService appleLoginService;
    private final NotificationService notificationService;

    /**
     * 회원가입
     */
    @Transactional(transactionManager = "transactionManager")
    public void signUp(SignUpRequest request) {
        // 아이디 고유성 확인
        if(usersRepository.existsByLoginId(request.loginId())) {
            throw new BusinessException(UserErrorCode.DUP_LOGIN_ID);
        }

        // 아이디 != 비밀번호 확인
        if(request.password().contains(request.loginId())) {
            throw new BusinessException(UserErrorCode.PASSWORD_CONTAINS_LOGIN_ID);
        }

        // 회원가입
        Users user = usersRepository.save(
                Users.create(request.loginId(), passwordEncoder.encode(request.password()))
        );

        // 스토어 로우 등록
        Store store = storeRepository.save(
                Store.create(user)
        );
    }

    /**
     * 로그인
     */
    @Transactional(transactionManager = "transactionManager")
    public LoginResponse login(LoginRequest request) {
        Users user = usersRepository.findByLoginId(request.loginId())
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOTFOUND_LOGIN_ID));

        if(!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException(UserErrorCode.INVALID_PASSWORD);
        }


        // 토큰 발급 및 저장
        String accessToken = jwtUtil.createAccessToken(user.getUserId());
        String refreshToken = jwtUtil.createRefreshToken(user.getUserId());

        saveRefreshToken(user.getUserId(), refreshToken);

        // 유저 최근 로그인 시간 업데이트
        user.updateLastLoginAt();

        if(request.fcmToken() != null) {
            // 알림 토큰 존재 시 저장
            notificationService.saveFcmToken(user.getUserId(), new FcmTokenRequest(request.fcmToken(),request.deviceType(), request.deviceId()));
        }

        return new LoginResponse(accessToken, refreshToken, user.getOnboardingCompleted());
    }

    /**
     * new access token 발급 요청
     */
    @Transactional(transactionManager = "transactionManager")
    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        // 토큰 유효기간, 타입 확인
        if(!jwtUtil.validateRefreshToken(request.refreshToken())) {
            throw new BusinessException(CommonErrorCode.INVALID_TOKEN);
        }

        // DB와 일치 여부 확인
        Long userId = jwtUtil.getUserId(request.refreshToken());

        if(!refreshTokenRepository.existsByUserIdAndRefreshToken(userId, request.refreshToken())) {
            throw new BusinessException(CommonErrorCode.INVALID_TOKEN);
        }

        // 새 Access token 발급
        return new TokenRefreshResponse(jwtUtil.createAccessToken(userId));
    }

    /* 로그아웃 (fcm 토큰 만료 처리) */
    @Transactional(transactionManager="transactionManager")
    public void logout(Long userId, LogoutRequest request) {
        // fcm 토큰 삭제
        if (request.fcmToken() != null) {
            fcmTokenRepository.deleteByToken(request.fcmToken());
        }
    }

    /* 로그아웃 (fcm 토큰 + refresh token 만료 처리) */
    @Transactional(transactionManager = "transactionManager")
    public void logoutWithRefreshToken(Long userId, LogoutWithRefreshTokenRequest request) {
        if (request.fcmToken() != null) {
            fcmTokenRepository.deleteByToken(request.fcmToken());
        }
        // 해당 refresh token 만 삭제 (다른 디바이스 세션 유지)
        if (request.refreshToken() != null) {
            refreshTokenRepository.deleteByUserIdAndRefreshToken(userId, request.refreshToken());
        }
    }


    // =========================================================================
    // 카카오 로그인
    // =========================================================================

    /** 웹 콜백 방식 (authorization code -> 서버에서 토큰 교환) **/
    @Transactional(transactionManager = "transactionManager")
    public LoginResponse kakaoLoginCallback(String code) {
        KakaoTokenResponse kakaoToken = kakaoLoginService.getToken(code);

        KakaoUserInfoResponse kakaoUserInfo = kakaoLoginService.getSubject(kakaoToken.accessToken());

        Users user = findOrCreateKakaoUser(kakaoUserInfo.id().toString());

        // Jwt 발급 및 저장
        return issueTokensAndRespond(user, null, null, null);
    }

    /** 모바일 방식 (앱에서 access token 전달) **/
    @Transactional(transactionManager = "transactionManager")
    public LoginResponse kakaoLogin(KakaoLoginRequest request) {
        // 토큰 검증
        KakaoAccessTokenValidateResponse kakaoAccessTokenValidateResponse = kakaoLoginService.validateAccessToken(request.accessToken());

        KakaoUserInfoResponse kakaoUserInfo = kakaoLoginService.getSubject(request.accessToken());

        Users user = findOrCreateKakaoUser(kakaoUserInfo.id().toString());
        return issueTokensAndRespond(user, request.fcmToken(), request.deviceType(), request.deviceId());
    }

    private Users findOrCreateKakaoUser(String kakaoId) {
        return usersRepository.findBySocialSubAndSocialProvider(kakaoId, "kakao")
                .orElseGet(() -> usersRepository.saveAndFlush(
                        Users.createKakaoUser(randomLoginId(), kakaoId)
                ));
    }

    // =========================================================================
    // 네이버 로그인
    // =========================================================================

    /** 웹 콜백 방식 */
    @Transactional(transactionManager = "transactionManager")
    public LoginResponse naverLoginCallback(String code, String state) {
        NaverTokenResponse naverToken = naverLoginService.getToken(code, state);

        NaverUserInfoResponse naverUserInfo = naverLoginService.getSubject(naverToken.accessToken());
        if (!naverUserInfo.resultcode().equals("00")) throw new BusinessException(SocialLoginErrorCode.NAVER_FORBIDDEN);

        Users user = findOrCreateNaverUser(naverUserInfo.response().getId());
        return issueTokensAndRespond(user, null, null, null);
    }

    // 네이버 로그인
    @Transactional(transactionManager = "transactionManager")
    public LoginResponse naverLogin(NaverLoginRequest request) {
        NaverUserInfoResponse naverUserInfo = naverLoginService.getSubject(request.accessToken());
        if (!naverUserInfo.resultcode().equals("00")) throw new BusinessException(SocialLoginErrorCode.NAVER_FORBIDDEN);

        Users user = findOrCreateNaverUser(naverUserInfo.response().getId());
        return issueTokensAndRespond(user, request.fcmToken(), request.deviceType(), request.deviceId());
    }


    private Users findOrCreateNaverUser(String naverId) {
        return usersRepository.findBySocialSubAndSocialProvider(naverId, "naver")
                .orElseGet(() -> usersRepository.saveAndFlush(
                        Users.createNaverUser(randomLoginId(), naverId)
                ));
    }

    // =========================================================================
    // 애플 로그인
    // =========================================================================
    @Transactional(transactionManager = "transactionManager")
    public LoginResponse appleLogin(AppleLoginRequest request) {
        AppleUserInfo appleUserInfo = appleLoginService.verifyAndExtract(request.identityToken());

        Users user = findOrCreateAppleUser(appleUserInfo.sub());
        return issueTokensAndRespond(user, request.fcmToken(), request.deviceType(), request.deviceId());

    }

    private Users findOrCreateAppleUser(String sub) {
        return usersRepository.findBySocialSubAndSocialProvider(sub, "apple")
                .orElseGet(() -> usersRepository.saveAndFlush(
                        Users.createAppleUser(randomLoginId(), sub)
                ));
    }

    /** 소셜 회원 loginId 랜덤 생성 */
    private String randomLoginId() {
        return "id" + UUID.randomUUID().toString().replace("-", "").substring(0, 15);
    }

    /** jwt 토큰 생성 및 LoginResponse 생성 */
    private LoginResponse issueTokensAndRespond(
            Users user, String fcmToken, String deviceType, String deviceId
    ) {
        String accessToken  = jwtUtil.createAccessToken(user.getUserId());
        String refreshToken = jwtUtil.createRefreshToken(user.getUserId());
        saveRefreshToken(user.getUserId(), refreshToken);
        user.updateLastLoginAt();

        if (fcmToken != null) {
            notificationService.saveFcmToken(
                    user.getUserId(),
                    new FcmTokenRequest(fcmToken, deviceType, deviceId)
            );
        }

        return new LoginResponse(accessToken, refreshToken, user.getOnboardingCompleted());
    }


    /** refresh token 생성 **/
    private void saveRefreshToken(Long userId, String refreshToken) {
        refreshTokenRepository.save(
                RefreshToken.create(userId, refreshToken, jwtUtil.getExpiration(refreshToken))
        );
    }
}
