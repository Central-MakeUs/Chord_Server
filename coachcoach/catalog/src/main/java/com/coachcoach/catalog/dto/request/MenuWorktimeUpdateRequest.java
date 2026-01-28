package com.coachcoach.catalog.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MenuWorktimeUpdateRequest {
    @NotNull(message = "제조시간 입력은 필수입니다.")
    @Positive(message = "제조시간은 0보다 커야 합니다.")
    private Integer workTime;
}
