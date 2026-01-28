package com.coachcoach.user.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Table(name = "tb_store")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    @Length(max = 20, min = 1)
    private String name;
    private Integer employees;
    @Column(scale = 10, precision = 1)
    private BigDecimal laborCost;
    @Column(scale = 15, precision = 2)
    private BigDecimal rentCost;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Store create(Long userId) {
        Store store = new Store();

        store.userId = userId;
        store.createdAt = LocalDateTime.now();
        store.updatedAt = LocalDateTime.now();

        return store;
    }
}