package com.coachcoach.catalog.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MenuNameUpdateRequest {
    @NotBlank(message = "메뉴명 입력은 필수입니다.")
    private String menuName;
}
