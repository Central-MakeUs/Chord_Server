package com.coachcoach.catalog.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class MenuCategoryUpdateRequest {
    @NotBlank(message = "카테고리 입력은 필수입니다.")
    private String category;
}
