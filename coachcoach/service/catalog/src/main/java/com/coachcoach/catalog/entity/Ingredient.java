package com.coachcoach.catalog.entity;

import com.coachcoach.catalog.entity.enums.Unit;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "tb_ingredient")
@Getter
@ToString
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ingredientId;
    private Long userId;
    private Long ingredientCategoryId;
    private String ingredientName;
    private Unit unit;
    @Column(precision = 10, scale = 2)
    private BigDecimal currentUnitPrice;
    private String supplier;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Ingredient create(
            Long userId, Long ingredientCategoryId,
            String ingredientName, Unit unit, BigDecimal currentUnitPrice
    ) {
        Ingredient ingredient = new Ingredient();

        ingredient.userId = userId;
        ingredient.ingredientCategoryId = ingredientCategoryId;

        ingredient.ingredientName = ingredientName;
        ingredient.unit = unit;
        ingredient.currentUnitPrice = currentUnitPrice;

        ingredient.createdAt = LocalDateTime.now();
        ingredient.updatedAt = LocalDateTime.now();

        return ingredient;
    }
}
