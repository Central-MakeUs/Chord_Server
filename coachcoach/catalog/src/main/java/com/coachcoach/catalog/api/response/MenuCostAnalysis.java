package com.coachcoach.catalog.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class MenuCostAnalysis {
    private BigDecimal costRate;             // 원가율
    private BigDecimal contributionMargin;   // 공헌이익률
    private BigDecimal marginRate;           // 마진율
    private String marginGradeCode;      // 마진 등급 코드
    private BigDecimal recommendedPrice;    // 권장 가격
}