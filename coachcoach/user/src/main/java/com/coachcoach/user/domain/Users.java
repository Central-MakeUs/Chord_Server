package com.coachcoach.user.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Builder
@Table(name = "tb_user")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    @Size(min = 3, max = 20)
    private String loginId;
    @Size(min = 8, max = 100)
    private String password;
    private LocalDateTime lastLoginAt;
    private Boolean onboardingCompleted = false;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String socialProvider = "local";
    private String socialSub;

    public static Users create(String loginId, String password) {
        LocalDateTime now = LocalDateTime.now();

        return Users.builder()
                .loginId(loginId)
                .password(password)
                .onboardingCompleted(false)
                .createdAt(now)
                .updatedAt(now)
                .socialProvider("local")
                .build();
    }

    public static Users createKakaoUser(String id, String sub) {
        LocalDateTime now = LocalDateTime.now();

        return Users.builder()
                .loginId(id)
                .password(null)
                .onboardingCompleted(false)
                .createdAt(now)
                .updatedAt(now)
                .socialProvider("kakao")
                .socialSub(sub)
                .build();
    }

    public static Users createNaverUser(String id, String sub) {
        LocalDateTime now = LocalDateTime.now();

        return Users.builder()
                .loginId(id)
                .password(null)
                .onboardingCompleted(false)
                .createdAt(now)
                .updatedAt(now)
                .socialProvider("naver")
                .socialSub(sub)
                .build();
    }

    public static Users createAppleUser(String id, String sub) {
        LocalDateTime now = LocalDateTime.now();

        return Users.builder()
                .loginId(id)
                .password(null)
                .onboardingCompleted(false)
                .createdAt(now)
                .updatedAt(now)
                .socialProvider("apple")
                .socialSub(sub)
                .build();
    }
    public void updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateOnboardingCompleted(Boolean onboardingCompleted) {
        this.onboardingCompleted = onboardingCompleted;
        this.updatedAt = LocalDateTime.now();
    }
}
