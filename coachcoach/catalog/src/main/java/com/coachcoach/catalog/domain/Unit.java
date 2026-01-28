package com.coachcoach.catalog.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Table(name = "tb_unit")
@Getter
@ToString
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Unit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long unitId;
    private String unitCode;
    private Integer baseQuantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
