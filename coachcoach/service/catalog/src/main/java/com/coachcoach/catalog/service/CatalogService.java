package com.coachcoach.catalog.service;

import com.coachcoach.catalog.entity.IngredientCategory;
import com.coachcoach.catalog.entity.MenuCategory;
import com.coachcoach.catalog.global.exception.CatalogErrorCode;
import com.coachcoach.catalog.repository.IngredientCategoryRepository;
import com.coachcoach.catalog.repository.MenuCategoryRepository;
import com.coachcoach.catalog.service.request.IngredientCategoryCreateRequest;
import com.coachcoach.catalog.service.request.MenuCategoryCreateRequest;
import com.coachcoach.catalog.service.response.IngredientCategoryResponse;
import com.coachcoach.catalog.service.response.MenuCategoryResponse;
import com.coachcoach.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogService {

    private final IngredientCategoryRepository ingredientCategoryRepository;
    private final MenuCategoryRepository menuCategoryRepository;

    /**
     * 재료 카테고리 생성
     */
    @Transactional
    public IngredientCategoryResponse createIngredientCategory(Long userId, IngredientCategoryCreateRequest request) {
        // 중복 여부 확인
        if(ingredientCategoryRepository.existsByUserIdAndCategoryName(
                userId, request.getCategoryName()
        )) {
            throw new BusinessException(CatalogErrorCode.DUPCATEGORY);
        }

        IngredientCategory ic = ingredientCategoryRepository.save(
                IngredientCategory.create(
                        userId,
                        request.getCategoryName()
                ));

        return IngredientCategoryResponse.from(ic);
    }

    /**
     * 메뉴 카테고리 생성
     */
    @Transactional
    public MenuCategoryResponse createMenuCategory(Long userId,  MenuCategoryCreateRequest request) {
        // 중복 여부 확인
        if(menuCategoryRepository.existsByUserIdAndCategoryName(
                userId, request.getCategoryName()
        )) {
            throw new BusinessException(CatalogErrorCode.DUPCATEGORY);
        }

        MenuCategory mc = menuCategoryRepository.save(
                MenuCategory.create(
                        userId,
                        request.getCategoryName()
                ));

        return MenuCategoryResponse.from(mc);
    }

    /**
     * 재료 카테고리 목록 조회
     */
    public List<IngredientCategoryResponse> readIngredientCategory(Long userId) {
        //정렬 조건: 생성한 시점 순
        List<IngredientCategory> result = ingredientCategoryRepository.findByUserIdOrderByCreatedAtAsc(userId);

        return result.stream()
                .map(IngredientCategoryResponse::from)
                .toList();
    }

    /**
     * 메뉴 카테고리 목록 조회
     */
    public List<MenuCategoryResponse> readMenuCategory(Long userId) {
        //정렬 조건: 생성한 시점 순
        List<MenuCategory> result = menuCategoryRepository.findByUserIdOrderByCreatedAtAsc(userId);

        return result.stream()
                .map(MenuCategoryResponse::from)
                .toList();
    }
}
