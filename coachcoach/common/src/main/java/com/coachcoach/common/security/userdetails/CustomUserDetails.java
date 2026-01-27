package com.coachcoach.common.security.userdetails;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@AllArgsConstructor
@Getter
public class CustomUserDetails implements UserDetails {

    private String userId;
    private String loginId;
    private String password;

    /**
     * 해당 유저의 권한 목록
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_USER")
        );
    }

    /**
     * 암호화된 비밀번호
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * 유저 PK
     */
    @Override
    public String getUsername() {
        return userId;
    }

    /**
     * 계정 만료 여부
     * true: 만료 안됨
     * false: 만료됨
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 계정 잠김 여부
     * true: 잠기지 않음
     * false: 잠김
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 비밀번호 만료 여부
     * true: 만료 안됨
     * false: 만료됨
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 사용자 활성화 여부
     * true: 활성화
     * false: 비활성화
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
