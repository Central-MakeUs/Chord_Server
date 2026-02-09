package com.coachcoach.catalog.repository;

import com.coachcoach.catalog.domain.TemplateMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemplateMenuRepository extends JpaRepository<TemplateMenu, Long> {
    @Query(
            value = "SELECT * " +
                    "FROM tb_template_menu t " +
                    "WHERE t.is_active = true " +
                    "AND (t.menu_name LIKE CONCAT('%', :keyword, '%') OR t.search_keywords LIKE CONCAT('%', :keyword, '%')) " +
                    "ORDER BY " +
                    "   CASE WHEN t.menu_name LIKE CONCAT('%', :keyword, '%') THEN 0 ELSE 1 END, " +
                    "   t.menu_name ASC",
            nativeQuery = true
    )
    List<TemplateMenu> findByKeywordWithPriority(
            @Param("keyword") String keyword
    );

}
