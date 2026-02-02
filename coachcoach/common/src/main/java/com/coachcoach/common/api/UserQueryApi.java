package com.coachcoach.common.api;

import com.coachcoach.common.dto.internal.StoreInfo;
import com.coachcoach.common.dto.internal.UserInfo;

public interface UserQueryApi {
    StoreInfo findStoreByUserId(Long userId);
    UserInfo findUserByUserId(Long userId);
}
