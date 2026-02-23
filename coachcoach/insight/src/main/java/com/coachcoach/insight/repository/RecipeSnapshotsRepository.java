package com.coachcoach.insight.repository;

import com.coachcoach.insight.domain.RecipeSnapshots;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeSnapshotsRepository extends JpaRepository<RecipeSnapshots, Long> {
}
