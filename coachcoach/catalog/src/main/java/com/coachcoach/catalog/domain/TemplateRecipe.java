package com.coachcoach.catalog.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
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
    private BigDecimal defaultCost;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
