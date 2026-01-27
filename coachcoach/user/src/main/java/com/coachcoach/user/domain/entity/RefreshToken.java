package com.coachcoach.user.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Table(name = "tb_refresh_token")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long refreshTokenId;
    private Long userId;
    @Column(columnDefinition = "TEXT")
    private String refreshToken;
    private LocalDateTime expiredAt;
    private LocalDateTime lastUsedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static RefreshToken create(
            Long userId,
            String refreshToken,
            LocalDateTime expiredAt
    ) {
        RefreshToken refreshtoken = new RefreshToken();

        refreshtoken.userId = userId;
        refreshtoken.refreshToken = refreshToken;
        refreshtoken.expiredAt = expiredAt;
        refreshtoken.createdAt = LocalDateTime.now();
        refreshtoken.updatedAt = LocalDateTime.now();

        return refreshtoken;
    }
}
