package com.coachcoach.catalog.domain.repository;

import com.coachcoach.catalog.domain.entity.TemplateIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateIngredientRepository extends JpaRepository<TemplateIngredient, Long> {
}
