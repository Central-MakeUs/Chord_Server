package com.coachcoach.catalog.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "tb_template_ingredient")
@Getter
@ToString
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TemplateIngredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ingredientTemplateId;
    private String ingredientCategoryCode;
    private String ingredientName;
    private String unitCode;
    @Column(precision = 10, scale = 2)
    private BigDecimal defaultUnitPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
