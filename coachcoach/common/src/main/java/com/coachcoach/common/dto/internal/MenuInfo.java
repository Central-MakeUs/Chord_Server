package com.coachcoach.common.dto.internal;

import lombok.Builder;

import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record MenuInfo(
        Long menuId,
        Long userId,
        String menuCategoryCode,
        String menuName,
        BigDecimal sellingPrice,
        BigDecimal totalCost,
        BigDecimal costRate,
        BigDecimal contributionMargin,
        BigDecimal marginRate,
        String marginGradeCode,
        Integer workTime,
        BigDecimal recommendedPrice,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
