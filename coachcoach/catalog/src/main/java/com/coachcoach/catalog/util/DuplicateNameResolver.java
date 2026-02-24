package com.coachcoach.catalog.util;

import com.coachcoach.catalog.domain.Ingredient;
import com.coachcoach.catalog.domain.TemplateIngredient;
import com.coachcoach.catalog.domain.TemplateRecipe;
import com.coachcoach.catalog.repository.IngredientRepository;
import com.coachcoach.catalog.repository.MenuRepository;
import com.coachcoach.catalog.exception.CatalogErrorCode;
import com.coachcoach.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    /**
     * 사용자에게 존재하는 재료로 대체
     */
    public Ingredient findMatchingIngredient(TemplateRecipe templateRecipe, TemplateIngredient templateIngredient, Map<String, Ingredient> userIngredientsMap) {
        List<String> candidates = new ArrayList<>();

        String targetName = templateIngredient.getIngredientName();
        candidates.add(targetName);

        for(int i = 1; i <= 4; ++i) {
            candidates.add(targetName + "(" + i + ")");
        }

        return candidates.stream()
                .filter(userIngredientsMap::containsKey)
                .findFirst()
                .map(userIngredientsMap::get)
                .orElse(null);
    }

}
