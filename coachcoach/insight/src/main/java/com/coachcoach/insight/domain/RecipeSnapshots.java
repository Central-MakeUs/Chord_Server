package com.coachcoach.insight.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "tb_recipe_snapshots")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@ToString
@Getter
public class RecipeSnapshots {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recipeSnapshotId;

    private Long snapshotId;

    private Long recipeId;

    private Long ingredientId;

    @Column(precision = 10, scale = 3)
    private BigDecimal amount;

    private String ingredientName;

    private String unitCode;

    @Column(precision = 10, scale = 2)
    private BigDecimal currentUnitPrice;

    private String supplier;

    private LocalDateTime createdAt;
}
