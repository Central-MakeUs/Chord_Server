package com.coachcoach.catalog.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MenuWorktimeUpdateRequest (
    @NotNull(message = "제조시간 입력은 필수입니다.")
    Integer workTime
) {}
