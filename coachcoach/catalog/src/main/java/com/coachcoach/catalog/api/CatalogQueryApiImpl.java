package com.coachcoach.catalog.api;

import com.coachcoach.catalog.repository.MenuRepository;
import com.coachcoach.common.api.CatalogQueryApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CatalogQueryApiImpl implements CatalogQueryApi {

    private final MenuRepository menuRepository;

    @Override
    public int countByUserIdAndMarginGradeCode(Long userId, String marginGradeCode) {
        return menuRepository.countByUserIdAndMarginGradeCode(userId, marginGradeCode);
    }
}
