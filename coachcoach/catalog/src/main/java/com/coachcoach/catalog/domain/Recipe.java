package com.coachcoach.catalog.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Table(name = "tb_recipe")
@Getter
@ToString
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recipeId;
    private Long menuId;
    private Long ingredientId;
    @Column(precision = 10, scale = 3)
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Recipe create(Long menuId, Long ingredientId, BigDecimal amount) {
        LocalDateTime now = LocalDateTime.now();

        return Recipe.builder()
                .menuId(menuId)
                .ingredientId(ingredientId)
                .amount(amount)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public void updateAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
