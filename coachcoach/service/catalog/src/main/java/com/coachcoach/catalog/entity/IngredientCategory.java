package com.coachcoach.catalog.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Table(name = "tb_ingredient_category")
@Getter
@Entity
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IngredientCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingredient_category_id")
    private Long categoryId;
    private Long userId;
    private String categoryName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static IngredientCategory create(Long userId, String categoryName) {
        IngredientCategory ic = new IngredientCategory();

        ic.userId = userId;
        ic.categoryName = categoryName;
        ic.createdAt = LocalDateTime.now();
        ic.updatedAt = LocalDateTime.now();

        return ic;
    }
}
