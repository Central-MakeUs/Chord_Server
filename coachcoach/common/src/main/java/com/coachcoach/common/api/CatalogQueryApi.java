package com.coachcoach.common.api;

import com.coachcoach.common.dto.internal.StoreInfo;
import com.coachcoach.common.dto.internal.UserInfo;

public interface CatalogQueryApi {
    int countByUserIdAndMarginGradeCode(Long userId, String marginGradeCode);

    void deleteByUserId(Long userId);
}
