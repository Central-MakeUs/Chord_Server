package com.coachcoach.insight.repository;

import com.coachcoach.insight.domain.MenuSnapshots;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuSnapshotsRepository extends JpaRepository<MenuSnapshots, Long> {
    List<MenuSnapshots> findByBaselineIdIn(List<Long> baselineIds);
    Optional<MenuSnapshots> findByBaselineIdAndMenuId(Long baselineId, Long menuId);
    List<MenuSnapshots> findByBaselineIdAndMenuIdIn(Long baselineId, List<Long> menuIds);
}
