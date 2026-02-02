package com.coachcoach.user.api;

import com.coachcoach.common.api.UserQueryApi;
import com.coachcoach.common.dto.internal.StoreInfo;
import com.coachcoach.common.dto.internal.UserInfo;
import com.coachcoach.common.exception.BusinessException;
import com.coachcoach.user.domain.Store;
import com.coachcoach.user.domain.Users;
import com.coachcoach.user.exception.UserErrorCode;
import com.coachcoach.user.repository.StoreRepository;
import com.coachcoach.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQueryApiImpl implements UserQueryApi {
    private final StoreRepository storeRepository;
    private final UsersRepository usersRepository;

    @Override
    public StoreInfo findStoreByUserId(Long userId) {
        Store store = storeRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOTFOUND_STORE));

        return StoreInfo.builder()
                .userId(store.getUserId())
                .name(store.getName())
                .employees(store.getEmployees())
                .laborCost(store.getLaborCost())
                .rentCost(store.getRentCost())
                .createdAt(store.getCreatedAt())
                .updatedAt(store.getUpdatedAt())
                .build();
    }

    @Override
    public UserInfo findUserByUserId(Long userId) {
        Users user = usersRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOTFOUND_USER));

        return UserInfo.builder()
                .userId(user.getUserId())
                .loginId(user.getLoginId())
                .lastLoginAt(user.getLastLoginAt())
                .onboardingCompleted(user.getOnboardingCompleted())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
