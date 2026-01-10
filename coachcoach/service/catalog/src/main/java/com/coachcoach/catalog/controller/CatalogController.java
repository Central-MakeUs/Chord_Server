package com.coachcoach.catalog.controller;

import com.coachcoach.catalog.service.CatalogService;
import com.coachcoach.catalog.service.response.IngredientCategoryResponse;
import com.coachcoach.catalog.service.response.MenuCategoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @RequestHeader(value = "userId", required = false)로 헤더 GET
 * return 자료형으로 원시 자료형 사용 불가 (무조건 DTO로 래핑 / 참조 자료형 사용)
 */

@RestController
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogService catalogService;

    /**
     * 재료 카테고리 목록 조회
     */
    @Operation(summary = "재료 카테고리 목록 조회", description = "📍인증 구현 X <br>📍display order를 기준으로 오름차순으로 반환<br>📍'전체'(ALL)는 목록에 포함되어 있지 않음.")
    @GetMapping("/ingredient-categories")
    public List<IngredientCategoryResponse> readIngredientCategory() {
        return catalogService.readIngredientCategory();
    }

    /**
     * 재료 생성
     */
//    @Operation(summary = "재료 생성", description = "📍인증 구현 X <br>📍유저가 중복 재료를 생성하려고 시도 시 CATALOG_002 에러 발생 (공백 구분 O)<br> 📍단위: G, KG, EA, ML")
//    @PostMapping("/ingredients")
//    public void createIngredient(
//            @RequestHeader(name = "userId", required = false) String userId,
//            @Valid @RequestBody IngredientCreateRequest request
//    ) {
//        catalogService.createIngredient(Long.valueOf(userId), request);
//    }


    /**
     * 메뉴 카테고리 목록 조회
     */
    @Operation(summary = "메뉴 카테고리 목록 조회", description = "📍인증 구현 X <br>📍display order를 기준으로 오름차순으로 반환📍'즐겨찾기(FAVORITE)'는 목록에 포함되어 있지 않음")
    @GetMapping("/menu-categories")
    public List<MenuCategoryResponse> readMenuCategory() {
        return catalogService.readMenuCategory();
    }
}
