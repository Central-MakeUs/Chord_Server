package com.coachcoach.user.domain.repository;

import com.coachcoach.user.domain.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users,Long> {
    Optional<Users> findByLoginId(String loginId);
    boolean existsByLoginId(String loginId);
}
