package com.coachcoach.user.service;

import com.coachcoach.common.exception.BusinessException;
import com.coachcoach.common.exception.CommonErrorCode;
import com.coachcoach.common.security.jwt.JwtUtil;
import com.coachcoach.user.domain.FcmToken;
import com.coachcoach.user.dto.request.*;
import com.coachcoach.user.dto.response.*;
import com.coachcoach.user.domain.RefreshToken;
import com.coachcoach.user.domain.Store;
import com.coachcoach.user.domain.Users;
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

        RefreshToken token = refreshTokenRepository.save(
                RefreshToken.create(
                        user.getUserId(),
                        refreshToken,
                        jwtUtil.getExpiration(refreshToken)
                )
        );

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
        String newAccessToken = jwtUtil.createAccessToken(userId);

        return new TokenRefreshResponse(newAccessToken);
    }

    @Transactional(transactionManager="transactionManager")
    public void logout(Long userId, LogoutRequest request) {
        // fcm 토큰 삭제
        fcmTokenRepository.deleteByToken(request.fcmToken());
    }


    // 카카오 로그인 콜백
    @Transactional(transactionManager = "transactionManager")
    public LoginResponse kakaoLoginCallback(String code) {
        KakaoTokenResponse kakaoToken = kakaoLoginService.getToken(code);

        if(kakaoToken.error() != null) {
            throw new BusinessException(UserErrorCode.SOCIAL_LOGIN_FAILED);
        }

        KakaoUserInfoResponse kakaoUserInfo = kakaoLoginService.getSubject(kakaoToken.accessToken());

        if(kakaoUserInfo.error() != null) {
            throw new BusinessException(UserErrorCode.SOCIAL_LOGIN_FAILED);
        }

        //기존 유저인지 조회
        Users user = usersRepository.findBySocialSubAndSocialProvider(kakaoUserInfo.id().toString(), "kakao")
                .orElseGet(() -> usersRepository.save(
                        Users.createKakaoUser("id" + UUID.randomUUID().toString().substring(3, 18), kakaoUserInfo.id().toString())
                ));

        // Jwt 발급 및 저장
        String accessToken = jwtUtil.createAccessToken(user.getUserId());
        String refreshToken = jwtUtil.createRefreshToken(user.getUserId());

        RefreshToken token = refreshTokenRepository.save(
                RefreshToken.create(
                        user.getUserId(),
                        refreshToken,
                        jwtUtil.getExpiration(refreshToken)
                )
        );

        // 유저 최근 로그인 시간 업데이트
        user.updateLastLoginAt();

        return new LoginResponse(accessToken, refreshToken, user.getOnboardingCompleted());
    }

    // 카카오 로그인
    @Transactional(transactionManager = "transactionManager")
    public LoginResponse kakaoLogin(KakaoLoginRequest request) {
        // 토큰 검증
        KakaoAccessTokenValidateResponse kakaoAccessTokenValidateResponse = kakaoLoginService.validateAccessToken(request.accessToken());

        if(kakaoAccessTokenValidateResponse.error() != null) {
            throw new BusinessException(UserErrorCode.SOCIAL_LOGIN_FAILED);
        }

        KakaoUserInfoResponse kakaoUserInfo = kakaoLoginService.getSubject(request.accessToken());

        if(kakaoUserInfo.error() != null) {
            throw new BusinessException(UserErrorCode.SOCIAL_LOGIN_FAILED);
        }

        //기존 유저인지 조회
        Users user = usersRepository.findBySocialSubAndSocialProvider(kakaoUserInfo.id().toString(), "kakao")
                .orElseGet(() -> usersRepository.save(
                        Users.createKakaoUser("id" + UUID.randomUUID().toString().substring(3, 18), kakaoUserInfo.id().toString())
                ));

        // Jwt 발급 및 저장
        String accessToken = jwtUtil.createAccessToken(user.getUserId());
        String refreshToken = jwtUtil.createRefreshToken(user.getUserId());

        RefreshToken token = refreshTokenRepository.save(
                RefreshToken.create(
                        user.getUserId(),
                        refreshToken,
                        jwtUtil.getExpiration(refreshToken)
                )
        );

        // 유저 최근 로그인 시간 업데이트
        user.updateLastLoginAt();

        if(request.fcmToken() != null) {
            // 알림 토큰 존재 시 저장
            notificationService.saveFcmToken(user.getUserId(), new FcmTokenRequest(request.fcmToken(),request.deviceType(), request.deviceId()));
        }

        return new LoginResponse(accessToken, refreshToken, user.getOnboardingCompleted());
    }

    // 네이버 로그인 콜백
    @Transactional(transactionManager = "transactionManager")
    public LoginResponse naverLoginCallback(String code, String state) {
        NaverTokenResponse naverToken = naverLoginService.getToken(code, state);

        if(naverToken.error() != null) {
            throw new BusinessException(UserErrorCode.SOCIAL_LOGIN_FAILED);
        }

        NaverUserInfoResponse naverUserInfo = naverLoginService.getSubject(naverToken.accessToken());

        if(!naverUserInfo.resultcode().equals("00")) {
            throw new BusinessException(UserErrorCode.SOCIAL_LOGIN_FAILED);
        }

        //기존 유저인지 조회
        Users user = usersRepository.findBySocialSubAndSocialProvider(naverUserInfo.response().getId(), "naver")
                .orElseGet(() -> usersRepository.save(
                        Users.createNaverUser("id" + UUID.randomUUID().toString().substring(3, 18) , naverUserInfo.response().getId())
                ));

        // Jwt 발급 및 저장
        String accessToken = jwtUtil.createAccessToken(user.getUserId());
        String refreshToken = jwtUtil.createRefreshToken(user.getUserId());

        RefreshToken token = refreshTokenRepository.save(
                RefreshToken.create(
                        user.getUserId(),
                        refreshToken,
                        jwtUtil.getExpiration(refreshToken)
                )
        );

        // 유저 최근 로그인 시간 업데이트
        user.updateLastLoginAt();

        return new LoginResponse(accessToken, refreshToken, user.getOnboardingCompleted());
    }

    // 네이버 로그인
    @Transactional(transactionManager = "transactionManager")
    public LoginResponse naverLogin(NaverLoginRequest request) {

        NaverUserInfoResponse naverUserInfo = naverLoginService.getSubject(request.accessToken());

        if(!naverUserInfo.resultcode().equals("00")) {
            throw new BusinessException(UserErrorCode.SOCIAL_LOGIN_FAILED);
        }

        //기존 유저인지 조회
        Users user = usersRepository.findBySocialSubAndSocialProvider(naverUserInfo.response().getId(), "naver")
                .orElseGet(() -> usersRepository.save(
                        Users.createNaverUser("id" + UUID.randomUUID().toString().substring(3, 18) , naverUserInfo.response().getId())
                ));

        // Jwt 발급 및 저장
        String accessToken = jwtUtil.createAccessToken(user.getUserId());
        String refreshToken = jwtUtil.createRefreshToken(user.getUserId());

        RefreshToken token = refreshTokenRepository.save(
                RefreshToken.create(
                        user.getUserId(),
                        refreshToken,
                        jwtUtil.getExpiration(refreshToken)
                )
        );

        // 유저 최근 로그인 시간 업데이트
        user.updateLastLoginAt();

        if(request.fcmToken() != null) {
            // 알림 토큰 존재 시 저장
            notificationService.saveFcmToken(user.getUserId(), new FcmTokenRequest(request.fcmToken(),request.deviceType(), request.deviceId()));
        }

        return new LoginResponse(accessToken, refreshToken, user.getOnboardingCompleted());
    }
}
