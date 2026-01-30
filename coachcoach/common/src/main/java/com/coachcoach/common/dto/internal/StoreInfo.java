package com.coachcoach.common.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record StoreInfo(
        Long userId,
        String name,
        Integer employees,
        BigDecimal laborCost,
        BigDecimal rentCost,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
