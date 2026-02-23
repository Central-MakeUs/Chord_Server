package com.coachcoach.insight.dto.response;

import java.time.LocalDate;
import java.util.List;

public record NeedManagement(
        LocalDate strategyDate,     // 추출 기준일
        List<NeedManagementMenu> menus  // 메뉴 목록
) {
}
