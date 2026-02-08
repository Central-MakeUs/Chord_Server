package com.coachcoach.user.service;

import com.coachcoach.common.api.CatalogQueryApi;
import com.coachcoach.common.api.InsightQueryApi;
import com.coachcoach.common.exception.BusinessException;
import com.coachcoach.user.domain.Store;
import com.coachcoach.user.domain.Users;
import com.coachcoach.user.dto.request.OnboardingRequest;
import com.coachcoach.user.exception.UserErrorCode;
import com.coachcoach.user.repository.RefreshTokenRepository;
import com.coachcoach.user.repository.StoreRepository;
import com.coachcoach.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final StoreRepository storeRepository;
    private final UsersRepository usersRepository;
    private final RefreshTokenRepository refreshTokenRepository;
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
                request.rentCost(),
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
    }
}
