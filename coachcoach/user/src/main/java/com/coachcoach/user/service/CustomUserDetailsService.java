package com.coachcoach.user.service;

import com.coachcoach.common.security.userdetails.CustomUserDetails;
import com.coachcoach.common.security.userdetails.UserDetailsLoader;
import com.coachcoach.user.domain.entity.Users;
import com.coachcoach.user.domain.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * spring security에서 유저 정보 GET
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService, UserDetailsLoader {

    private final UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        return loadUserById(Long.valueOf(userId));
    }

    @Override
    public UserDetails loadUserById(Long userId) throws UsernameNotFoundException {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId));

        return convertToUserDetails(user);
    }

    /**
     * Users 엔티티 -> CustomUserDetail
     * @param user
     * @return
     */
    private CustomUserDetails convertToUserDetails(Users user) {
        return new CustomUserDetails(
                user.getUserId().toString(),
                user.getLoginId(),
                user.getPassword()
        );
    }
}
