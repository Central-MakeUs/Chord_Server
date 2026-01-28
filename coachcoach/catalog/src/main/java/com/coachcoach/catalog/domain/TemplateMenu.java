package com.coachcoach.catalog.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "tb_template_menu")
@Getter
@ToString
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TemplateMenu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long templateId;
    private String menuCategoryCode;
    private String menuName;
    @Column(precision = 10, scale = 2)
    private BigDecimal defaultSellingPrice;        // 판매가
    private Integer workTime;               // 제조 소요 시간(s)
    @Column(name = "search_keywords", columnDefinition = "TEXT")
    private String searchKeywords;
    @Column(name = "is_active")
    private Boolean isActive = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
