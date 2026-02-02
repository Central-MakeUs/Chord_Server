package com.coachcoach.catalog.dto.request;

import jakarta.validation.constraints.NotBlank;

public record MenuNameUpdateRequest (
        @NotBlank(message = "메뉴명 입력은 필수입니다.")
        String menuName
) {}