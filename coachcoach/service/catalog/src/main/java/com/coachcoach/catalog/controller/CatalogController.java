package com.coachcoach.catalog.controller;

import com.coachcoach.catalog.service.CatalogService;
import com.coachcoach.catalog.service.request.IngredientCategoryCreateRequest;
import com.coachcoach.catalog.service.request.IngredientCreateRequest;
import com.coachcoach.catalog.service.request.MenuCategoryCreateRequest;
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
     * 재료 카테고리 생성
     */
    @Operation(summary = "재료 카테고리 생성", description = "📍인증 구현 X <br>📍유저가 중복 카테고리를 생성하려고 시도 시 CATALOG_001 에러 발생")
    @PostMapping("/ingredients/category")
    public IngredientCategoryResponse createIngredientCategory(
            @RequestHeader(name = "userId", required = false) String userId,
            @Valid @RequestBody IngredientCategoryCreateRequest request
    ) {
        return catalogService.createIngredientCategory(Long.valueOf(userId), request);
    }

    /**
     * 재료 카테고리 목록 조회
     */
    @Operation(summary = "재료 카테고리 목록 조회", description = "📍인증 구현 X <br>📍유저 별 생성한 재료 카테고리 목록 조회(생성 시간 기준 오름차순)")
    @GetMapping("/ingredients/category")
    public List<IngredientCategoryResponse> readIngredientCategory(@RequestHeader(name = "userId", required = false) String userId) {
        return catalogService.readIngredientCategory(Long.valueOf(userId));
    }

    /**
     * 재료 생성
     */
    @Operation(summary = "재료 생성", description = "📍인증 구현 X <br>📍유저가 중복 재료를 생성하려고 시도 시 CATALOG_002 에러 발생 (공백 구분 O)")
    @PostMapping("/ingredients")
    public void createIngredient(
            @RequestHeader(name = "userId", required = false) String userId,
            @Valid @RequestBody IngredientCreateRequest request
    ) {
        catalogService.createIngredient(Long.valueOf(userId), request);
    }

    /**
     * 메뉴 카테고리 생성
     */
    @Operation(summary = "메뉴 카테고리 생성", description = "📍인증 구현 X <br>📍유저가 중복 카테고리를 생성하려고 시도 시 CATALOG_001 에러 발생")
    @PostMapping("/menu/category")
    public MenuCategoryResponse createMenuCategory(
            @RequestHeader(name = "userId", required = false) String userId,
            @Valid @RequestBody MenuCategoryCreateRequest request
    ) {
        return catalogService.createMenuCategory(Long.valueOf(userId), request);
    }

    /**
     * 메뉴 카테고리 목록 조회
     */
    @Operation(summary = "메뉴 카테고리 목록 조회", description = "📍인증 구현 X <br>📍유저 별 생성한 메뉴 카테고리 목록 조회(생성 시간 기준 오름차순)")
    @GetMapping("/menu/category")
    public List<MenuCategoryResponse> readMenuCategory(@RequestHeader(name = "userId", required = false) String userId) {
        return catalogService.readMenuCategory(Long.valueOf(userId));
    }
}
