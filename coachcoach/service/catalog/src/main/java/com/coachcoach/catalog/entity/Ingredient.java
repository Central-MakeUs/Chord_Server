package com.coachcoach.catalog.entity;

import com.coachcoach.catalog.global.util.Cache;
import jakarta.persistence.*;
import lombok.*;

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
    private String ingredientCategoryCode;
    private String ingredientName;
    private String unitCode;
    @Column(precision = 10, scale = 2)
    private BigDecimal currentUnitPrice;
    private String supplier;
    @Column(name = "is_favorite")
    private Boolean favorite = false;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Ingredient create(
            Long userId,
            String ingredientCategoryCode,
            String ingredientName,
            String unitCode,
            BigDecimal currentUnitPrice,
            String supplier
    ) {
        Ingredient ingredient = new Ingredient();

        ingredient.userId = userId;
        ingredient.ingredientCategoryCode = ingredientCategoryCode;
        ingredient.ingredientName = ingredientName;
        ingredient.unitCode = unitCode;
        ingredient.currentUnitPrice = currentUnitPrice;
        ingredient.supplier = supplier;
        ingredient.createdAt = LocalDateTime.now();
        ingredient.updatedAt = LocalDateTime.now();

        return ingredient;
    }

    public void updateFavorite(Boolean favorite) {
        this.favorite = favorite;
        this.updatedAt = LocalDateTime.now();
    }
}
