package com.coachcoach.user.repository;

import com.coachcoach.user.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users,Long> {
    Optional<Users> findByLoginId(String loginId);
    boolean existsByLoginId(String loginId);
}
