package com.coachcoach.catalog.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
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

    public void update(
            String ingredientCategoryCode,
            BigDecimal currentUnitPrice,
            String unitCode

    ) {
        this.ingredientCategoryCode = ingredientCategoryCode;
        this.currentUnitPrice = currentUnitPrice;
        this.unitCode = unitCode;
        this.updatedAt = LocalDateTime.now();
    }

    public static Ingredient create(
            Long userId,
            String ingredientCategoryCode,
            String ingredientName,
            String unitCode,
            BigDecimal currentUnitPrice,
            String supplier
    ) {
        LocalDateTime now = LocalDateTime.now();
        return Ingredient.builder()
                .userId(userId)
                .ingredientCategoryCode(ingredientCategoryCode)
                .ingredientName(ingredientName)
                .unitCode(unitCode)
                .currentUnitPrice(currentUnitPrice)
                .supplier(supplier)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public void updateFavorite(Boolean favorite) {
        this.favorite = favorite;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateSupplier(String supplier) {
        this.supplier = supplier;
    }
}