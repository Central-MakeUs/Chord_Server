package com.coachcoach.catalog.global.util;

import com.coachcoach.catalog.domain.repository.IngredientRepository;
import com.coachcoach.catalog.domain.repository.MenuRepository;
import com.coachcoach.catalog.global.exception.CatalogErrorCode;
import com.coachcoach.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DuplicateNameResolver {

    private final MenuRepository menuRepository;
    private final IngredientRepository ingredientRepository;

    /**
     * 중복 이름 해결 로직 (메뉴)
     */
    public String createNonDupMenuName(Long userId, String originalName) {
        if (!menuRepository.existsByUserIdAndMenuName(userId, originalName)) {
            return originalName;
        }

        // 최대 (4)까지 생성 가능

        for(int i = 1; i <= 4; ++i) {
            String candidateName = originalName + String.format("(%d)", i);
            if(!menuRepository.existsByUserIdAndMenuName(userId, candidateName)) {
                return candidateName;
            }
        }

        throw new BusinessException(CatalogErrorCode.INVALID_MENU_NAME);
    }

    /**
     * 중복 이름 해결 로직 (재료)
     */
    public String createNonDupIngredientName(Long userId, String originalName) {
        if(!ingredientRepository.existsByUserIdAndIngredientName(userId, originalName)) {
            return originalName;
        }

        // 최대 (4)까지 생성 가능

        for(int i = 1; i <= 4; ++i) {
            String candidateName = originalName + String.format("(%d)", i);
            if(!ingredientRepository.existsByUserIdAndIngredientName(userId, candidateName)) {
                return candidateName;
            }
        }

        throw new BusinessException(CatalogErrorCode.INVALID_INGREDIENT_NAME);
    }
}
