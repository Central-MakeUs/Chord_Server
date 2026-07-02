package com.coachcoach.user.domain;

import com.coachcoach.user.dto.request.OnboardingRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Table(name = "tb_store")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class Store {

    @Id
    private Long userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private Users user;

    @Size(max = 20, min = 1)
    private String name;
    private Integer employees;
    @Column(precision = 10, scale = 1)
    private BigDecimal laborCost;
    @Column(precision = 15, scale = 2)
    private BigDecimal rentCost;
    private Boolean includeWeeklyHolidayPay = false;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Store create(Users user) {
        LocalDateTime now = LocalDateTime.now();

        return Store.builder()
                .user(user)
                .includeWeeklyHolidayPay(false)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public void updateInformation(
            String name,
            Integer employees,
            BigDecimal laborCost,
            Boolean includeWeeklyHolidayPay
    ) {
        this.name = name;
        this.employees = employees;
        this.laborCost = laborCost;
        this.includeWeeklyHolidayPay = includeWeeklyHolidayPay;
        this.updatedAt = LocalDateTime.now();
    }
}