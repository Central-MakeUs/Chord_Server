package com.coachcoach.user.service;

import com.coachcoach.common.exception.BusinessException;
import com.coachcoach.user.domain.Store;
import com.coachcoach.user.domain.Users;
import com.coachcoach.user.dto.request.OnboardingRequest;
import com.coachcoach.user.exception.UserErrorCode;
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

    @Transactional(transactionManager = "userTransactionManager")
    public void onboarding(Long userId, OnboardingRequest request) {
        Store store = storeRepository.findByUserId(userId).orElse(Store.create(userId));
        Users user = usersRepository.findByUserId(userId).orElseThrow(() -> new BusinessException(UserErrorCode.NOTFOUND_USER));

        store.updateInformation(
                request.name(),
                request.employees(),
                request.laborCost(),
                request.rentCost(),
                request.includeWeeklyHolidayPay()
        );

        user.updateOnboardingCompleted(true);
    }
}
