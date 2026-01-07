package com.coachcoach.catalog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Table(name = "tb_menu_category")
@Getter
@Entity
@ToString
@NoArgsConstructor
public class MenuCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_category_id")
    private Long categoryId;
    private Long userId;
    private String categoryName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MenuCategory create(Long userId, String categoryName) {
        MenuCategory mc = new MenuCategory();

        mc.userId = userId;
        mc.categoryName = categoryName;
        mc.createdAt = LocalDateTime.now();
        mc.updatedAt = LocalDateTime.now();

        return mc;
    }
}
