package com.coachcoach.catalog.controller;

import com.coachcoach.catalog.service.CatalogService;
import com.coachcoach.catalog.service.request.IngredientCreateRequest;
import com.coachcoach.catalog.service.request.IngredientUpdateRequest;
import com.coachcoach.catalog.service.request.SupplierUpdateRequest;
import com.coachcoach.catalog.service.response.*;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "재료 카테고리 목록 조회", description = "📍인증 구현 X <br>📍display order를 기준으로 오름차순으로 반환<br>📍'즐겨찾기(FAVORITE)'는 목록에 포함되어 있지 않음")
    @GetMapping("/ingredient-categories")
    public List<IngredientCategoryResponse> readIngredientCategory() {
        return catalogService.readIngredientCategory();
    }

    /**
     * 카테고리 별 재료 목록 반환 (필터링, 복수 선택 가능)
     */
    @Operation(summary = "카테고리 별 재료 목록 반환")
    @GetMapping("/ingredients")
    public List<IngredientResponse> readIngredientsByCategory(@RequestHeader(name = "userId", required = false) String userId, @RequestParam(name = "category", required = false) List<String> category) {
        return catalogService.readIngredientsByCategory(Long.valueOf(userId), category);
    }

    /**
     * 재료 생성
     */
    @Operation(summary = "재료 생성", description = "📍인증 구현 X <br>📍유저가 중복 재료를 생성하려고 시도 시 CATALOG_002 에러 발생 (공백 구분 O)<br> 📍단위: G, KG, EA, ML")
    @PostMapping("/ingredients")
    public IngredientResponse createIngredient(
            @RequestHeader(name = "userId", required = false) String userId,
            @Valid @RequestBody IngredientCreateRequest request
    ) {
        return catalogService.createIngredient(Long.valueOf(userId), request);
    }

    /**
     * 재료 상세
     */
    @Operation(summary = "재료 상세")
    @GetMapping("/ingredients/{ingredientId}")
    public IngredientDetailResponse readIngredientDetail(
            @RequestHeader(name = "userId", required = false) String userId,
            @PathVariable(name = "ingredientId") Long ingredientId
    ) {
        return catalogService.readIngredientDetail(Long.valueOf(userId), ingredientId);
    }

    /**
     * 가격 변경 이력 목록
     */
    @Operation(summary = "재료 가격 변경 이력 목록")
    @GetMapping("/ingredients/{ingredientId}/price-history")
    public List<PriceHistoryResponse> readIngredientPriceHistory(
            @RequestHeader(name = "userId", required = false) String userId,
            @PathVariable(name = "ingredientId") Long ingredientId
    ) {
        return catalogService.readIngredientPriceHistory(Long.valueOf(userId), ingredientId);
    }

    /**
     * 즐겨찾기 설정/해제
     */
    @Operation(summary = "즐겨찾기 설정/해제")
    @PatchMapping("/ingredients/{ingredientId}/favorite")
    public void updateFavorite(
            @RequestHeader(name = "userId", required = false) String userId,
            @PathVariable(name = "ingredientId") Long ingredientId,
            @RequestParam(name = "favorite") Boolean favorite
    ) {
        catalogService.updateFavorite(Long.valueOf(userId), ingredientId, favorite);
    }

    /**
     * 재료 단가 수정
     */
    @Operation(summary = "재료 단가 수정")
    @PatchMapping("/ingredients/{ingredientId}")
    public IngredientUpdateResponse updateIngredient(
            @RequestHeader(name = "userId", required = false) String userId,
            @PathVariable(name = "ingredientId") Long ingredientId,
            @Valid @RequestBody IngredientUpdateRequest request
    ) {
        return catalogService.updateIngredient(Long.valueOf(userId), ingredientId, request);
    }

    /**
     * 재료 공급업체 수정
     */
    @Operation(summary = "메뉴 공급업체 수정")
    @PatchMapping("/ingredients/{ingredientId}/supplier")
    public SupplierUpdateResponse updateIngredientSupplier(
            @RequestHeader(name = "userId", required = false) String userId,
            @PathVariable(name = "ingredientId") Long ingredientId,
            @RequestBody SupplierUpdateRequest request
    ) {
        return catalogService.updateIngredientSupplier(Long.valueOf(userId), ingredientId, request);
    }

    /**
     * 메뉴 카테고리 목록 조회
     */
    @Operation(summary = "메뉴 카테고리 목록 조회", description = "📍인증 구현 X <br>📍display order를 기준으로 오름차순으로 반환<br>📍'전체'(ALL)는 목록에 포함되어 있지 않음")
    @GetMapping("/menu-categories")
    public List<MenuCategoryResponse> readMenuCategory() {
        return catalogService.readMenuCategory();
    }
}
