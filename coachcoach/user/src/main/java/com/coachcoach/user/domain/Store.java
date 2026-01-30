package com.coachcoach.user.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Table(name = "tb_store")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class Store {

    @Id
    private Long userId;
    @Length(max = 20, min = 1)
    private String name;
    private Integer employees;
    @Column(scale = 10, precision = 1)
    private BigDecimal laborCost;
    @Column(scale = 15, precision = 2)
    private BigDecimal rentCost;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Store create(Long userId) {
        LocalDateTime now = LocalDateTime.now();

        return Store.builder()
                .userId(userId)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}