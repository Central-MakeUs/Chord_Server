package com.coachcoach.common.security.userdetails;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserDetailsLoader {
    /**
     * userId로 사용자 조회
     */
    UserDetails loadUserById(Long userId) throws UsernameNotFoundException;
}
