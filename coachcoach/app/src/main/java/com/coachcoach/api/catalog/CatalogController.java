package com.coachcoach.api.catalog;

import com.coachcoach.catalog.dto.request.*;
import com.coachcoach.catalog.dto.response.*;
import com.coachcoach.catalog.service.MenuService;
import com.coachcoach.catalog.service.IngredientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
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
            @RequestHeader(name = "userId", required = false, defaultValue = "1") String userId,
            @RequestParam(name = "category", required = false) List<String> category
    ) {
        return ingredientService.readIngredientsByCategory(Long.valueOf(userId), category);
    }

    /**
     * 재료 상세 조회
     */
    @Operation(summary = "재료 상세 조회")
    @GetMapping("/ingredients/{ingredientId}")
    public IngredientDetailResponse readIngredientDetail(
            @RequestHeader(name = "userId", required = false, defaultValue = "1") String userId,
            @PathVariable(name = "ingredientId") @Positive Long ingredientId
    ) {
        return ingredientService.readIngredientDetail(Long.valueOf(userId), ingredientId);
    }

    /**
     * 가격 변경 이력 목록 조회
     */
    @Operation(summary = "재료 가격 변경 이력 목록 조회")
    @GetMapping("/ingredients/{ingredientId}/price-history")
    public List<PriceHistoryResponse> readIngredientPriceHistory(
            @RequestHeader(name = "userId", required = false, defaultValue = "1") String userId,
            @PathVariable(name = "ingredientId") @Positive Long ingredientId
    ) {
        return ingredientService.readIngredientPriceHistory(Long.valueOf(userId), ingredientId);
    }

    /**
     * 재료 검색 (in template & users)
     */
    @Operation(summary = "재료 검색 (템플릿 & 유저가 등록한 재료 내)")
    @GetMapping("/ingredients/search")
    public List<SearchIngredientsResponse> searchIngredients(
            @RequestHeader(name = "userId", required = false, defaultValue = "1") String userId,
            @RequestParam(name = "keyword", required = false) String keyword
    ) {
        return ingredientService.searchIngredients(Long.valueOf(userId), keyword);
    }

    /**
     * 재료명 중복 확인
     */
    @Operation(summary = "재료명 중복 확인")
    @GetMapping("/ingredients/check-dup")
    public void checkDupIngredientName(
            @RequestHeader(name = "userId", required = false, defaultValue = "1") String userId,
            @RequestParam(name = "name") @NotBlank @Size(max = 100) String ingredientName
    ) {
        ingredientService.checkDupIngredientName(Long.valueOf(userId), ingredientName);
    }

    /**
     * 재료 검색 (with 재료명, 메뉴명)
     */
    @Operation(summary = "재료 검색(with 재료명, 메뉴명)")
    @GetMapping("/ingredients/search/my")
    public List<SearchMyIngredientsResponse> searchMyIngredients(
            @RequestHeader(name = "userId", required = false, defaultValue = "1") String userId,
            @RequestParam(name = "keyword", required = false) String keyword
    ) {
        return ingredientService.searchMyIngredients(Long.valueOf(userId), keyword);
    }

    /* -------------생성------------- */
    /**
     * 재료 생성
     */
    @Operation(summary = "재료 생성")
    @PostMapping("/ingredients")
    public IngredientResponse createIngredient(
            @RequestHeader(name = "userId", required = false, defaultValue = "1") String userId,
            @Valid @RequestBody IngredientCreateRequest request
    ) {
        return ingredientService.createIngredient(Long.valueOf(userId), request);
    }

    /* -------------수정------------- */
    /**
     * 즐겨찾기 설정/해제
     */
    @Operation(summary = "즐겨찾기 설정/해제")
    @PatchMapping("/ingredients/{ingredientId}/favorite")
    public void updateFavorite(
            @RequestHeader(name = "userId", required = false, defaultValue = "1") String userId,
            @PathVariable(name = "ingredientId") @Positive Long ingredientId,
            @RequestParam(name = "favorite") Boolean favorite
    ) {
        ingredientService.updateFavorite(Long.valueOf(userId), ingredientId, favorite);
    }

    /**
     * 재료 공급업체 수정
     */
    @Operation(summary = "재료 공급업체 수정")
    @PatchMapping("/ingredients/{ingredientId}/supplier")
    public void updateIngredientSupplier(
            @RequestHeader(name = "userId", required = false, defaultValue = "1") String userId,
            @PathVariable(name = "ingredientId") @Positive Long ingredientId,
            @RequestBody SupplierUpdateRequest request
    ) {
        ingredientService.updateIngredientSupplier(Long.valueOf(userId), ingredientId, request);
    }

    /**
     * 재료 단가 수정 -> 해당 재료 사용하는 모든 메뉴에 대해 업데이트 필요
     */
    @Operation(summary = "재료 단가 수정")
    @PatchMapping("/ingredients/{ingredientId}")
    public void updateIngredientPrice(
            @RequestHeader(name = "userId", required = false, defaultValue = "1") String userId,
            @RequestHeader(name = "laborCost", required = false, defaultValue = "10320") String laborCost,
            @PathVariable(name = "ingredientId") @Positive Long ingredientId,
            @Valid @RequestBody IngredientUpdateRequest request
    ) {
        ingredientService.updateIngredientPrice(Long.valueOf(userId), BigDecimal.valueOf(Long.parseLong(laborCost)), ingredientId, request);
    }

    /* -------------삭제------------- */
    /**
     * 재료 삭제 -> 해당 재료 사용하는 모든 메뉴 업데이트 필요
     */
    @Operation(summary = "재료 삭제")
    @DeleteMapping("/ingredients/{ingredientId}")
    public void deleteIngredient(
            @RequestHeader(name = "userId", required = false, defaultValue = "1") String userId,
            @RequestHeader(name = "laborCost", required = false, defaultValue = "10320") String laborCost,
            @PathVariable(name = "ingredientId") @Positive Long ingredientId
    ) {
        ingredientService.deleteIngredient(Long.valueOf(userId), BigDecimal.valueOf(Long.parseLong(laborCost)), ingredientId);
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
            @RequestHeader(name = "userId", required = false, defaultValue = "1") String userId,
            @RequestParam(name = "category", required = false) String category
    ) {
        return menuService.readMenusByCategory(Long.valueOf(userId), category);
    }

    /**
     * 메뉴 상세 정보 조회
     */
    @Operation(summary = "메뉴 상세 정보 조회")
    @GetMapping("/menus/{menuId}")
    public MenuDetailResponse readMenu(
            @RequestHeader(name = "userId", required = false, defaultValue = "1") String userId,
            @PathVariable(name = "menuId") @Positive Long menuId
    ) {
        return menuService.readMenu(Long.valueOf(userId), menuId);
    }

    /**
     * 메뉴 상세 정보 - 레시피 목록 조회
     */
    @Operation(summary = "메뉴 상세 정보 - 레시피 목록 조회")
    @GetMapping("/menus/{menuId}/recipes")
    public RecipeListResponse readRecipes(
            @RequestHeader(name = "userId", required = false, defaultValue = "1") String userId,
            @PathVariable(name = "menuId") @Positive Long menuId
    ) {
        return menuService.readRecipes(Long.valueOf(userId), menuId);
    }

    /**
     * 메뉴명 + 재료명 중복 확인 (일괄)
     */
    @Operation(summary = "메뉴명 + 재료명 중복 확인 (일괄)")
    @PostMapping("/menus/check-dup")
    public CheckDupResponse checkDupNames(
            @RequestHeader(name = "userId", required = false, defaultValue = "1") String userId,
            @Valid @RequestBody CheckDupRequest request
    ) {
        return menuService.checkDupNames(Long.valueOf(userId), request);
    }

    /* -------------생성------------- */

    /**
     * 메뉴 생성
     */
    @Operation(summary = "메뉴 생성")
    @PostMapping("/menus")
    public void createMenu(
            @RequestHeader(name = "userId", required = false, defaultValue = "1") String userId,
            @RequestHeader(name = "laborCost", required = false, defaultValue = "10320") String laborCost,
            @Valid @RequestBody MenuCreateRequest request
    ) {
        menuService.createMenu(Long.valueOf(userId), BigDecimal.valueOf(Long.parseLong(laborCost)), request);
    }

    /**
     * 레시피 추가 (단일 / 기존 재료)
     */
    @Operation(summary = "레시피 추가 (단일 / 기존 재료)")
    @PostMapping("/menus/{menuId}/recipes/existing")
    public void createRecipe(
            @RequestHeader(name = "userId", required = false, defaultValue = "1") String userId,
            @RequestHeader(name = "laborCost", required = false, defaultValue = "10320") String laborCost,
            @PathVariable(name = "menuId") @Positive Long menuId,
            @Valid @RequestBody RecipeCreateRequest request
    ) {
        menuService.createRecipe(Long.valueOf(userId), BigDecimal.valueOf(Long.parseLong(laborCost)), menuId, request);
    }

    /**
     * 레시피 추가 (단일 / 새 재료)
     */
    @Operation(summary = "레시피 추가(단일 / 새 재료)")
    @PostMapping("/menus/{menuId}/recipes/new")
    public void createRecipeWithNew(
            @RequestHeader(name = "userId", required = false, defaultValue = "1") String userId,
            @RequestHeader(name = "laborCost", required = false, defaultValue = "10320") String laborCost,
            @PathVariable(name = "menuId") @Positive Long menuId,
            @Valid @RequestBody NewRecipeCreateRequest request
    ) {
        menuService.createRecipeWithNew(Long.valueOf(userId), BigDecimal.valueOf(Long.parseLong(laborCost)), menuId, request);
    }

    /* -------------수정------------- */

    /**
     * 메뉴명 수정
     */
    @Operation(summary = "메뉴명 수정")
    @PatchMapping("/menus/{menuId}")
    public void updateMenuName(
            @RequestHeader(name = "userId", required = false, defaultValue = "1") String userId,
            @PathVariable(name = "menuId") @Positive Long menuId,
            @Valid @RequestBody MenuNameUpdateRequest request
    ) {
        menuService.updateMenuName(Long.valueOf(userId), menuId, request.getMenuName());
    }

    /**
     * 메뉴 판매가 수정
     */
    @Operation(summary = "메뉴 판매가 수정")
    @PatchMapping("/menus/{menuId}/price")
    public void updateSellingPrice(
            @RequestHeader(name = "userId", required = false, defaultValue = "1") String userId,
            @RequestHeader(name = "laborCost", required = false, defaultValue = "10320") String laborCost,
            @PathVariable(name = "menuId") @Positive Long menuId,
            @Valid @RequestBody MenuPriceUpdateRequest request
    ) {
        menuService.updateSellingPrice(Long.valueOf(userId), BigDecimal.valueOf(Long.parseLong(laborCost)), menuId, request.getSellingPrice());
    }

    /**
     * 메뉴 카테고리 수정
     */
    @Operation(summary = "메뉴 카테고리 수정")
    @PatchMapping("/menus/{menuId}/category")
    public void updateMenuCategory(
            @RequestHeader(name = "userId", required = false, defaultValue = "1") String userId,
            @PathVariable(name = "menuId") @Positive Long menuId,
            @Valid @RequestBody MenuCategoryUpdateRequest request
    ) {
        menuService.updateMenuCategory(Long.valueOf(userId), menuId, request.getCategory());
    }

    /**
     * 메뉴 제조시간 수정
     */
    @Operation(summary = "메뉴 제조시간 수정")
    @PatchMapping("/menus/{menuId}/worktime")
    public void updateWorkTime(
            @RequestHeader(name = "userId", required = false, defaultValue = "1") String userId,
            @RequestHeader(name = "laborCost", required = false, defaultValue = "10320") String laborCost,
            @PathVariable(name = "menuId") @Positive Long menuId,
            @Valid @RequestBody MenuWorktimeUpdateRequest request
    ) {
        menuService.updateWorkTime(Long.valueOf(userId), BigDecimal.valueOf(Long.parseLong(laborCost)), menuId, request.getWorkTime());
    }

    /**
     * 레시피 수정 (only 사용량)
     */
    @Operation(summary = "레시피 수정 (only 사용량)")
    @PatchMapping("/menus/{menuId}/recipes/{recipeId}")
    public void updateRecipe(
            @RequestHeader(name = "userId", required = false, defaultValue = "1") String userId,
            @RequestHeader(name = "laborCost", required = false, defaultValue = "10320") String laborCost,
            @PathVariable(name = "menuId") @Positive Long menuId,
            @PathVariable(name = "recipeId") @Positive Long recipeId,
            @RequestBody AmountUpdateRequest request
    ) {
        menuService.updateRecipe(Long.valueOf(userId), BigDecimal.valueOf(Long.parseLong(laborCost)), menuId, recipeId, request.getAmount());
    }

    /* -------------삭제------------- */
    /**
     * 레시피 삭제 (복수 선택 가능) -> 해당 메뉴 정보 업데이트 필요
     */
    @Operation(summary = "레시피 삭제 (복수 선택 가능)")
    @DeleteMapping("/menus/{menuId}/recipes")
    public void deleteRecipes(
            @RequestHeader(name = "userId", required = false, defaultValue = "1") String userId,
            @RequestHeader(name = "laborCost", required = false, defaultValue = "10320") String laborCost,
            @PathVariable(name = "menuId") @Positive Long menuId,
            @Valid @RequestBody DeleteRecipesRequest request
    ) {
        menuService.deleteRecipes(Long.valueOf(userId), BigDecimal.valueOf(Long.parseLong(laborCost)), menuId, request);
    }

    /**
     * 메뉴 삭제 (단일)
     */
    @Operation(summary = "메뉴 삭제(단일)")
    @DeleteMapping("/menus/{menuId}")
    public void deleteMenu(
            @RequestHeader(name = "userId", required = false, defaultValue = "1") String userId,
            @PathVariable(name = "menuId") @Positive Long menuId
    ) {
        menuService.deleteMenu(Long.valueOf(userId), menuId);
    }
}
