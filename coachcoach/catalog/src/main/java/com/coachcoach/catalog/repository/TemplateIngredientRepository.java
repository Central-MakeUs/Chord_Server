package com.coachcoach.catalog.repository;

import com.coachcoach.catalog.domain.TemplateIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemplateIngredientRepository extends JpaRepository<TemplateIngredient, Long> {
    @Query(
            value = "SELECT * " +
                    "FROM tb_template_ingredient ti " +
                    "WHERE ti.ingredient_name LIKE CONCAT('%', :keyword, '%') " +
                    "ORDER BY ti.ingredient_name ASC",
            nativeQuery = true
    )
    List<TemplateIngredient> findByKeywordOrderByIngredientNameAsc(String keyword);
}
