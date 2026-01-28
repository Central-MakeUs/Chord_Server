package com.coachcoach.user.repository;

import com.coachcoach.user.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store,Long> {
}
