package com.coachcoach.catalog.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "tb_template_recipe")
@Getter
@ToString
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TemplateRecipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recipeTemplateId;
    private Long templateId;
    private Long ingredientTemplateId;
    @Column(precision = 10, scale = 3)
    private BigDecimal defaultUsageAmount;
    @Column(precision = 10, scale = 2)
    private BigDecimal defaultPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
