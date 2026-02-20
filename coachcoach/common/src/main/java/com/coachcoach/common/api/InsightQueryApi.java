package com.coachcoach.common.api;

import java.time.LocalDate;

public interface InsightQueryApi {
    void deleteByUserId(Long userId);
    int getNumOfDangerMenus(Long userId);
}
