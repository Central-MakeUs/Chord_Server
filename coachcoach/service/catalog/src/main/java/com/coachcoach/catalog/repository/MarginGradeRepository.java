package com.coachcoach.catalog.repository;

import com.coachcoach.catalog.entity.MarginGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarginGradeRepository extends JpaRepository<MarginGrade, Long> {
    List<MarginGrade> findAllByOrderByGradeIdAsc();
}
