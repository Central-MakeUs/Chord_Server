package com.coachcoach.catalog.domain.repository;

import com.coachcoach.catalog.domain.entity.TemplateRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemplateRecipeRepository extends JpaRepository<TemplateRecipe, Long> {
    List<TemplateRecipe> findByTemplateIdOrderByRecipeTemplateIdAsc(Long templateId);
}
