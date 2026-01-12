package com.coachcoach.catalog.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private BigDecimal usageAmount;
    @Column(precision = 10, scale = 2)
    private BigDecimal cost;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
