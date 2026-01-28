package com.coachcoach.user.domain;

import jakarta.persistence.*;
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
    @Length(min = 3, max = 20)
    private String loginId;
    @Length(min = 8, max = 100)
    private String password;
    private LocalDateTime lastLoginAt;
    private Boolean onboardingCompleted = false;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Users create(String loginId, String password) {
        LocalDateTime now = LocalDateTime.now();

        return Users.builder()
                .loginId(loginId)
                .password(password)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public void updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
    }
}
