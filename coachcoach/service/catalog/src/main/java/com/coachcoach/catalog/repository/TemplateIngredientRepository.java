package com.coachcoach.catalog.repository;

import com.coachcoach.catalog.entity.TemplateIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TemplateIngredientRepository extends JpaRepository<TemplateIngredient, Long> {
}
