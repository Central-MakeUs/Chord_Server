package com.coachcoach.user.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Table(name = "tb_refresh_token")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
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
        LocalDateTime now = LocalDateTime.now();

        return RefreshToken.builder()
                .userId(userId)
                .refreshToken(refreshToken)
                .expiredAt(expiredAt)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
