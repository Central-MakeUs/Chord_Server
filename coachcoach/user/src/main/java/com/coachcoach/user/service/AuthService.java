package com.coachcoach.user.service;

import com.coachcoach.common.exception.BusinessException;
import com.coachcoach.common.security.jwt.JwtUtil;
import com.coachcoach.user.dto.request.LoginRequest;
import com.coachcoach.user.dto.request.SignUpRequest;
import com.coachcoach.user.dto.response.LoginResponse;
import com.coachcoach.user.domain.RefreshToken;
import com.coachcoach.user.domain.Store;
import com.coachcoach.user.domain.Users;
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
        if(usersRepository.existsByLoginId(request.getLoginId())) {
            throw new BusinessException(UserErrorCode.DUP_LOGIN_ID);
        }

        // 아이디 != 비밀번호 확인
        if(request.getPassword().contains(request.getLoginId())) {
            throw new BusinessException(UserErrorCode.INVALID_PASSWORD);
        }

        // 회원가입
        Users user = usersRepository.save(
                Users.create(request.getLoginId(), passwordEncoder.encode(request.getPassword()))
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
        Users user = usersRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOTFOUND_LOGIN_ID));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
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

        return LoginResponse.of(accessToken, refreshToken);
    }
}
