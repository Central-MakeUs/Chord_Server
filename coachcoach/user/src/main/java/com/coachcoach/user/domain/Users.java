package com.coachcoach.user.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Table(name = "tb_user")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
        Users user = new Users();

        user.loginId = loginId;
        user.password = password;
        user.createdAt = LocalDateTime.now();
        user.updatedAt = LocalDateTime.now();

        return user;
    }

    public void updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
    }
}
