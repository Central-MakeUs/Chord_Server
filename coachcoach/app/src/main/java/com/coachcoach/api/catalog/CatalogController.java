package com.coachcoach.api.catalog;

import com.coachcoach.catalog.dto.request.*;
import com.coachcoach.catalog.dto.response.*;
import com.coachcoach.catalog.service.MenuService;
import com.coachcoach.catalog.service.IngredientService;
import com.coachcoach.common.security.userdetails.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;


/**
 * @RequestHeader(value = "userId", required = false)로 헤더 GET
 * return 자료형으로 원시 자료형 사용 불가 (무조건 DTO로 래핑 / 참조 자료형 사용)
 */

@Tag(name = "카탈로그", description = "카탈로그 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/catalog")
public class CatalogController {

    private final MenuService menuService;
    private final IngredientService ingredientService;

    /* -------------재료------------- */

    /* -------------조회------------- */
    /**
     * 재료 카테고리 목록 조회
     */
    @Operation(summary = "재료 카테고리 목록 조회")
    @GetMapping("/ingredient-categories")
    public List<IngredientCategoryResponse> readIngredientCategory() {
        return ingredientService.readIngredientCategory();
    }

    /**
     * 카테고리 별 재료 목록 조회(필터링, 복수 선택 가능)
     */
    @Operation(summary = "카테고리 별 재료 목록 조회")
    @GetMapping("/ingredients")
    public List<IngredientResponse> readIngredientsByCategory(
            @AuthenticationPrincipal CustomUserDetails details,
            @RequestParam(name = "category", required = false) List<String> category
    ) {
        return ingredientService.readIngredientsByCategory(Long.valueOf(details.getUserId()), category);
    }

    /**
     * 재료 상세 조회
     */
    @Operation(summary = "재료 상세 조회")
    @GetMapping("/ingredients/{ingredientId}")
    public IngredientDetailResponse readIngredientDetail(
            @AuthenticationPrincipal CustomUserDetails details,
            @PathVariable(name = "ingredientId") @Positive Long ingredientId
    ) {
        return ingredientService.readIngredientDetail(Long.valueOf(details.getUserId()), ingredientId);
    }

    /**
     * 가격 변경 이력 목록 조회
     */
    @Operation(summary = "재료 가격 변경 이력 목록 조회")
    @GetMapping("/ingredients/{ingredientId}/price-history")
    public List<PriceHistoryResponse> readIngredientPriceHistory(
            @AuthenticationPrincipal CustomUserDetails details,
            @PathVariable(name = "ingredientId") @Positive Long ingredientId
    ) {
        return ingredientService.readIngredientPriceHistory(Long.valueOf(details.getUserId()), ingredientId);
    }

    /**
     * 재료 검색 (in template & users)
     */
    @Operation(summary = "재료 검색 (템플릿 & 유저가 등록한 재료 내)")
    @GetMapping("/ingredients/search")
    public List<SearchIngredientsResponse> searchIngredients(
            @AuthenticationPrincipal CustomUserDetails details,
            @RequestParam(name = "keyword", required = false) String keyword
    ) {
        return ingredientService.searchIngredients(Long.valueOf(details.getUserId()), keyword);
    }

    /**
     * 재료명 중복 확인
     */
    @Operation(summary = "재료명 중복 확인")
    @GetMapping("/ingredients/check-dup")
    public void checkDupIngredientName(
            @AuthenticationPrincipal CustomUserDetails details,
            @RequestParam(name = "name") @NotBlank @Size(max = 100) String ingredientName
    ) {
        ingredientService.checkDupIngredientName(Long.valueOf(details.getUserId()), ingredientName);
    }

    /**
     * 재료 검색 (with 재료명, 메뉴명)
     */
    @Operation(summary = "재료 검색(with 재료명, 메뉴명)")
    @GetMapping("/ingredients/search/my")
    public List<SearchMyIngredientsResponse> searchMyIngredients(
            @AuthenticationPrincipal CustomUserDetails details,
            @RequestParam(name = "keyword", required = false) String keyword
    ) {
        return ingredientService.searchMyIngredients(Long.valueOf(details.getUserId()), keyword);
    }

    /* -------------생성------------- */
    /**
     * 재료 생성
     */
    @Operation(summary = "재료 생성")
    @PostMapping("/ingredients")
    public IngredientResponse createIngredient(
            @AuthenticationPrincipal CustomUserDetails details,
            @Valid @RequestBody IngredientCreateRequest request
    ) {
        return ingredientService.createIngredient(Long.valueOf(details.getUserId()), request);
    }

    /* -------------수정------------- */
    /**
     * 즐겨찾기 설정/해제
     */
    @Operation(summary = "즐겨찾기 설정/해제")
    @PatchMapping("/ingredients/{ingredientId}/favorite")
    public void updateFavorite(
            @AuthenticationPrincipal CustomUserDetails details,
            @PathVariable(name = "ingredientId") @Positive Long ingredientId,
            @RequestParam(name = "favorite") Boolean favorite
    ) {
        ingredientService.updateFavorite(Long.valueOf(details.getUserId()), ingredientId, favorite);
    }

    /**
     * 재료 공급업체 수정
     */
    @Operation(summary = "재료 공급업체 수정")
    @PatchMapping("/ingredients/{ingredientId}/supplier")
    public void updateIngredientSupplier(
            @AuthenticationPrincipal CustomUserDetails details,
            @PathVariable(name = "ingredientId") @Positive Long ingredientId,
            @RequestBody SupplierUpdateRequest request
    ) {
        ingredientService.updateIngredientSupplier(Long.valueOf(details.getUserId()), ingredientId, request);
    }

    /**
     * 재료 단가 수정 -> 해당 재료 사용하는 모든 메뉴에 대해 업데이트 필요
     */
    @Operation(summary = "재료 단가 수정")
    @PatchMapping("/ingredients/{ingredientId}")
    public void updateIngredientPrice(
            @AuthenticationPrincipal CustomUserDetails details,

            @PathVariable(name = "ingredientId") @Positive Long ingredientId,
            @Valid @RequestBody IngredientUpdateRequest request
    ) {
        // todo: laborCost 수정
        ingredientService.updateIngredientPrice(Long.valueOf(details.getUserId()), ingredientId, request);
    }

    /* -------------삭제------------- */
    /**
     * 재료 삭제 -> 해당 재료 사용하는 모든 메뉴 업데이트 필요
     */
    @Operation(summary = "재료 삭제")
    @DeleteMapping("/ingredients/{ingredientId}")
    public void deleteIngredient(
            @AuthenticationPrincipal CustomUserDetails details,

            @PathVariable(name = "ingredientId") @Positive Long ingredientId
    ) {
        ingredientService.deleteIngredient(Long.valueOf(details.getUserId()), ingredientId);
    }

    /* -------------메뉴------------- */

    /* -------------조회------------- */
    /**
     * 메뉴 카테고리 목록 조회
     */
    @Operation(summary = "메뉴 카테고리 목록 조회")
    @GetMapping("/menu-categories")
    public List<MenuCategoryResponse> readMenuCategory() {
        return menuService.readMenuCategory();
    }

    /**
     * 메뉴명 검색
     * todo: 유사도 기반 나열
     */
    @Operation(summary = "메뉴명 검색")
    @GetMapping("/menus/search")
    public List<SearchMenusResponse> searchMenus(@RequestParam(name = "keyword", required = false) String keyword) {
        return menuService.searchMenus(keyword);
    }

    /**
     * 템플릿에 따른 메뉴 기본 정보 제공 (메뉴명 + 가격 + 카테고리 + 제조시간)
     */
    @Operation(summary = "템플릿에 따른 메뉴 기본 정보 제공 (메뉴명+가격+카테고리+제조시간)")
    @GetMapping("/menus/templates/{templateId}")
    public TemplateBasicResponse readMenuTemplate(@PathVariable(name = "templateId") @Positive Long templateId) {
        return menuService.readMenuTemplate(templateId);
    }

    /**
     * 템플릿에 따른 재료 리스트 제공
     */
    @Operation(summary = "템플릿에 따른 재료 리스트 제공")
    @GetMapping("/menus/templates/{templateId}/ingredients")
    public List<RecipeTemplateResponse> readTemplateIngredients(@PathVariable(name = "templateId") @Positive Long templateId) {
        return menuService.readTemplateIngredients(templateId);
    }


    /**
     * 카테고리 별 메뉴 목록 반환 (필터링)
     */
    @Operation(summary = "카테고리 별 메뉴 목록 반환 (필터링)")
    @GetMapping("/menus")
    public List<MenuResponse> readMenusByCategory(
            @AuthenticationPrincipal CustomUserDetails details,
            @RequestParam(name = "category", required = false) String category
    ) {
        return menuService.readMenusByCategory(Long.valueOf(details.getUserId()), category);
    }

    /**
     * 메뉴 상세 정보 조회
     */
    @Operation(summary = "메뉴 상세 정보 조회")
    @GetMapping("/menus/{menuId}")
    public MenuDetailResponse readMenu(
            @AuthenticationPrincipal CustomUserDetails details,
            @PathVariable(name = "menuId") @Positive Long menuId
    ) {
        return menuService.readMenu(Long.valueOf(details.getUserId()), menuId);
    }

    /**
     * 메뉴 상세 정보 - 레시피 목록 조회
     */
    @Operation(summary = "메뉴 상세 정보 - 레시피 목록 조회")
    @GetMapping("/menus/{menuId}/recipes")
    public RecipeListResponse readRecipes(
            @AuthenticationPrincipal CustomUserDetails details,
            @PathVariable(name = "menuId") @Positive Long menuId
    ) {
        return menuService.readRecipes(Long.valueOf(details.getUserId()), menuId);
    }

    /**
     * 메뉴명 + 재료명 중복 확인 (일괄)
     */
    @Operation(summary = "메뉴명 + 재료명 중복 확인 (일괄)")
    @PostMapping("/menus/check-dup")
    public CheckDupResponse checkDupNames(
            @AuthenticationPrincipal CustomUserDetails details,
            @Valid @RequestBody CheckDupRequest request
    ) {
        return menuService.checkDupNames(Long.valueOf(details.getUserId()), request);
    }

    /* -------------생성------------- */

    /**
     * 메뉴 생성
     */
    @Operation(summary = "메뉴 생성")
    @PostMapping("/menus")
    public void createMenu(
            @AuthenticationPrincipal CustomUserDetails details,

            @Valid @RequestBody MenuCreateRequest request
    ) {
        menuService.createMenu(Long.valueOf(details.getUserId()), request);
    }

    /**
     * 레시피 추가 (단일 / 기존 재료)
     */
    @Operation(summary = "레시피 추가 (단일 / 기존 재료)")
    @PostMapping("/menus/{menuId}/recipes/existing")
    public void createRecipe(
            @AuthenticationPrincipal CustomUserDetails details,

            @PathVariable(name = "menuId") @Positive Long menuId,
            @Valid @RequestBody RecipeCreateRequest request
    ) {
        menuService.createRecipe(Long.valueOf(details.getUserId()), menuId, request);
    }

    /**
     * 레시피 추가 (단일 / 새 재료)
     */
    @Operation(summary = "레시피 추가(단일 / 새 재료)")
    @PostMapping("/menus/{menuId}/recipes/new")
    public void createRecipeWithNew(
            @AuthenticationPrincipal CustomUserDetails details,

            @PathVariable(name = "menuId") @Positive Long menuId,
            @Valid @RequestBody NewRecipeCreateRequest request
    ) {
        menuService.createRecipeWithNew(Long.valueOf(details.getUserId()), menuId, request);
    }

    /* -------------수정------------- */

    /**
     * 메뉴명 수정
     */
    @Operation(summary = "메뉴명 수정")
    @PatchMapping("/menus/{menuId}")
    public void updateMenuName(
            @AuthenticationPrincipal CustomUserDetails details,
            @PathVariable(name = "menuId") @Positive Long menuId,
            @Valid @RequestBody MenuNameUpdateRequest request
    ) {
        menuService.updateMenuName(Long.valueOf(details.getUserId()), menuId, request.menuName());
    }

    /**
     * 메뉴 판매가 수정
     */
    @Operation(summary = "메뉴 판매가 수정")
    @PatchMapping("/menus/{menuId}/price")
    public void updateSellingPrice(
            @AuthenticationPrincipal CustomUserDetails details,

            @PathVariable(name = "menuId") @Positive Long menuId,
            @Valid @RequestBody MenuPriceUpdateRequest request
    ) {
        menuService.updateSellingPrice(Long.valueOf(details.getUserId()), menuId, request.sellingPrice());
    }

    /**
     * 메뉴 카테고리 수정
     */
    @Operation(summary = "메뉴 카테고리 수정")
    @PatchMapping("/menus/{menuId}/category")
    public void updateMenuCategory(
            @AuthenticationPrincipal CustomUserDetails details,
            @PathVariable(name = "menuId") @Positive Long menuId,
            @Valid @RequestBody MenuCategoryUpdateRequest request
    ) {
        menuService.updateMenuCategory(Long.valueOf(details.getUserId()), menuId, request.category());
    }

    /**
     * 메뉴 제조시간 수정
     */
    @Operation(summary = "메뉴 제조시간 수정")
    @PatchMapping("/menus/{menuId}/worktime")
    public void updateWorkTime(
            @AuthenticationPrincipal CustomUserDetails details,

            @PathVariable(name = "menuId") @Positive Long menuId,
            @Valid @RequestBody MenuWorktimeUpdateRequest request
    ) {
        menuService.updateWorkTime(Long.valueOf(details.getUserId()), menuId, request.workTime());
    }

    /**
     * 레시피 수정 (only 사용량)
     */
    @Operation(summary = "레시피 수정 (only 사용량)")
    @PatchMapping("/menus/{menuId}/recipes/{recipeId}")
    public void updateRecipe(
            @AuthenticationPrincipal CustomUserDetails details,

            @PathVariable(name = "menuId") @Positive Long menuId,
            @PathVariable(name = "recipeId") @Positive Long recipeId,
            @RequestBody AmountUpdateRequest request
    ) {
        menuService.updateRecipe(Long.valueOf(details.getUserId()), menuId, recipeId, request.amount());
    }

    /* -------------삭제------------- */
    /**
     * 레시피 삭제 (복수 선택 가능) -> 해당 메뉴 정보 업데이트 필요
     */
    @Operation(summary = "레시피 삭제 (복수 선택 가능)")
    @DeleteMapping("/menus/{menuId}/recipes")
    public void deleteRecipes(
            @AuthenticationPrincipal CustomUserDetails details,

            @PathVariable(name = "menuId") @Positive Long menuId,
            @Valid @RequestBody DeleteRecipesRequest request
    ) {
        menuService.deleteRecipes(Long.valueOf(details.getUserId()), menuId, request);
    }

    /**
     * 메뉴 삭제 (단일)
     */
    @Operation(summary = "메뉴 삭제(단일)")
    @DeleteMapping("/menus/{menuId}")
    public void deleteMenu(
            @AuthenticationPrincipal CustomUserDetails details,
            @PathVariable(name = "menuId") @Positive Long menuId
    ) {
        menuService.deleteMenu(Long.valueOf(details.getUserId()), menuId);
    }
}
