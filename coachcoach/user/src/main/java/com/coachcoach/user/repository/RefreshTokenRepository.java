package com.coachcoach.user.repository;

import com.coachcoach.user.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    boolean existsByUserIdAndRefreshToken(Long userId, String refreshToken);
}
