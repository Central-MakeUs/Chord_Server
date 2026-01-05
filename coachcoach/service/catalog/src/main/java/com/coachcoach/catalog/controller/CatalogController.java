package com.coachcoach.catalog.controller;

import com.coachcoach.catalog.service.CatalogService;
import com.coachcoach.catalog.service.request.IngredientCategoryCreateRequest;
import com.coachcoach.catalog.service.response.IngredientCategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;


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
    @PostMapping("/ingredients/category")
    public IngredientCategoryResponse createIngredientCategory(@RequestHeader(name = "userId", required = false) String userId, @RequestBody IngredientCategoryCreateRequest request) {
        return catalogService.createIngredientCategory(Long.valueOf(userId), request);
    }
}
