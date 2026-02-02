package com.coachcoach.catalog.dto.request;

import jakarta.validation.constraints.NotBlank;


public record MenuCategoryUpdateRequest(
    @NotBlank(message = "카테고리 입력은 필수입니다.")
    String category
) {}