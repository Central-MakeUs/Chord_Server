package com.coachcoach.user.api;

import com.coachcoach.common.api.UserQueryApi;
import com.coachcoach.common.dto.internal.StoreInfo;
import com.coachcoach.common.exception.BusinessException;
import com.coachcoach.user.domain.Store;
import com.coachcoach.user.exception.UserErrorCode;
import com.coachcoach.user.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQueryApiImpl implements UserQueryApi {
    private final StoreRepository storeRepository;


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
}
