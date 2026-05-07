package com.coachcoach.notification.repository;

import com.coachcoach.notification.domain.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FcmTokenRepository extends JpaRepository<FcmToken,Long> {
    Optional<FcmToken> findByUserId(Long userId);
    void deleteByToken(String token);
    void deleteAllByUserId(Long userId);
    List<FcmToken> findAllByUserIdAndDeviceTypeAndDeviceId(Long userId, String deviceType, String deviceId);
}
