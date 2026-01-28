package com.coachcoach.catalog.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class CheckDupRequest {
    @NotBlank(message = "메뉴명 입력은 필수입니다.")
    private String menuName;
    private List<String> ingredientNames;
}
