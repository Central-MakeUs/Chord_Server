package com.coachcoach.catalog.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Table(name = "tb_margin_grade")
@Getter
@ToString
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MarginGrade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gradeId;
    private String gradeCode;
    private String gradeName;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
