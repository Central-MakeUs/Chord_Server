package com.coachcoach.catalog.dto.response;

import java.math.BigDecimal;

public record MenusInUse(
        String menuName,
        BigDecimal amount,
        String unitCode
) {
}
