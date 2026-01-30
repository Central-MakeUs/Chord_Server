package com.coachcoach.common.api;

import com.coachcoach.common.dto.internal.StoreInfo;

public interface UserQueryApi {
    StoreInfo findStoreByUserId(Long userId);
}
