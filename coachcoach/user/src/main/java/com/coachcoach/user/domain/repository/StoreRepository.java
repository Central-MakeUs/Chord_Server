package com.coachcoach.user.domain.repository;

import com.coachcoach.user.domain.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store,Long> {
}
