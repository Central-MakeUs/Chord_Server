package com.coachcoach.user.service;

import com.coachcoach.common.exception.BusinessException;
import com.coachcoach.common.exception.CommonErrorCode;
import com.coachcoach.common.security.jwt.JwtUtil;
import com.coachcoach.user.dto.request.LoginRequest;
import com.coachcoach.user.dto.request.SignUpRequest;
import com.coachcoach.user.dto.request.TokenRefreshRequest;
import com.coachcoach.user.dto.response.LoginResponse;
import com.coachcoach.user.domain.RefreshToken;
import com.coachcoach.user.domain.Store;
import com.coachcoach.user.domain.Users;
import com.coachcoach.user.dto.response.TokenRefreshResponse;
import com.coachcoach.user.repository.RefreshTokenRepository;
import com.coachcoach.user.repository.StoreRepository;
import com.coachcoach.user.repository.UsersRepository;
import com.coachcoach.user.exception.UserErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsersRepository usersRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final StoreRepository storeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 회원가입
     */
    @Transactional
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
                Store.create(user.getUserId())
        );
    }

    /**
     * 로그인
     */
    @Transactional
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

        return new LoginResponse(accessToken, refreshToken);
    }

    /**
     * new access token 발급 요청
     */
    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        // 토큰 유효기간, 타입 확인
        if(jwtUtil.validateRefreshToken(request.refreshToken())) {
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
}
