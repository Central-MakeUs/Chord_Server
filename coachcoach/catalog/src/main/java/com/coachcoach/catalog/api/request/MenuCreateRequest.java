package com.coachcoach.catalog.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Getter
@ToString
public class MenuCreateRequest {
    // 메뉴 관련
    @NotBlank(message = "메뉴 카테고리 입력은 필수입니다.")
    private String menuCategoryCode;
    @NotBlank(message = "메뉴 이름 입력은 필수입니다.")
    private String menuName;
    @NotNull(message = "판매가 입력은 필수입니다.")
    private BigDecimal sellingPrice;
    @NotNull(message = "제조시간 입력은 필수입니다.")
    private Integer workTime;

    // 재료 관련
    List<IngredientCreateRequest> ingredients;
}
