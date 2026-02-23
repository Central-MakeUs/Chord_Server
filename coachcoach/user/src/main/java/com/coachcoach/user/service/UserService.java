package com.coachcoach.user.service;

import com.coachcoach.common.api.CatalogQueryApi;
import com.coachcoach.common.api.InsightQueryApi;
import com.coachcoach.common.exception.BusinessException;
import com.coachcoach.common.security.userdetails.CustomUserDetails;
import com.coachcoach.user.domain.Store;
import com.coachcoach.user.domain.Users;
import com.coachcoach.user.dto.request.LogoutRequest;
import com.coachcoach.user.dto.request.OnboardingRequest;
import com.coachcoach.user.dto.request.UpdateStoreRequest;
import com.coachcoach.user.dto.response.StoreResponse;
import com.coachcoach.user.exception.UserErrorCode;
import com.coachcoach.user.repository.FcmTokenRepository;
import com.coachcoach.user.repository.RefreshTokenRepository;
import com.coachcoach.user.repository.StoreRepository;
import com.coachcoach.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final StoreRepository storeRepository;
    private final UsersRepository usersRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final CatalogQueryApi catalogQueryApi;
    private final InsightQueryApi insightQueryApi;

    /**
     * 온보딩
     */
    @Transactional(transactionManager = "transactionManager")
    public void onboarding(Long userId, OnboardingRequest request) {
        Users user = usersRepository.findByUserId(userId).orElseThrow(() -> new BusinessException(UserErrorCode.NOTFOUND_USER));
        Store store = storeRepository.findByUserId(userId).orElse(Store.create(user));

        store.updateInformation(
                request.name(),
                request.employees(),
                request.laborCost(),
                request.includeWeeklyHolidayPay()
        );

        user.updateOnboardingCompleted(true);
    }

    /**
     * 회원 탈퇴
     * insights -> recipes -> menus -> ingredients -> ingredient price histories -> refresh tokens -> stores -> users
     */
    @Transactional(transactionManager = "transactionManager")
    public void deleteUser(Long userId) {

        // delete insights
        insightQueryApi.deleteByUserId(userId);

        // delete catalogs
        catalogQueryApi.deleteByUserId(userId);

        // delete user information
        refreshTokenRepository.deleteByUserId(userId);
        storeRepository.deleteByUserId(userId);
        usersRepository.deleteByUserId(userId);

        // delete fcm tokens
        fcmTokenRepository.deleteAllByUserId(userId);
    }

    /**
     * 매장 정보 수정 (매장 이름 + 인건비 + 직원 수)
     */
    @Transactional(transactionManager = "transactionManager")
    public void updateStore(Long userId, UpdateStoreRequest request) {
        Store store = storeRepository.findByUserId(userId).orElseThrow(() -> new BusinessException(UserErrorCode.NOTFOUND_STORE));
        BigDecimal prevLaborCost = store.getLaborCost();

        store.updateInformation(
                request.name(),
                request.employees(),
                request.laborCost(),
                request.includeWeeklyHolidayPay()
        );

        //인건비 수정에 따른 메뉴 값 변화 업데이트 (공헌이익, 마진률)
        if(prevLaborCost.compareTo(request.laborCost()) != 0) {
            catalogQueryApi.updateMenusByUpdateLaborCost(userId, request.laborCost(), request.includeWeeklyHolidayPay());
        }
    }

    public StoreResponse getStore(
            Long userId
    ) {
        Store store = storeRepository.findByUserId(userId).orElseThrow(() -> new BusinessException(UserErrorCode.NOTFOUND_STORE));

        return new StoreResponse(
                store.getName(),
                store.getEmployees(),
                store.getLaborCost(),
                store.getRentCost(),
                store.getIncludeWeeklyHolidayPay()
        );
    }
}
