package com.coachcoach.user.repository;

import com.coachcoach.user.domain.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FcmTokenRepository extends JpaRepository<FcmToken,Long> {
    Optional<FcmToken> findByUserId(Long userId);
}
