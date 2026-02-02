package com.coachcoach.catalog.dto.response;

import java.math.BigDecimal;

public record MenuCostAnalysis(
    BigDecimal costRate,             // 원가율
    BigDecimal contributionMargin,   // 공헌이익률
    BigDecimal marginRate,           // 마진율
    String marginGradeCode,      // 마진 등급 코드
    BigDecimal recommendedPrice    // 권장 가격
) {}