package com.coachcoach.user.domain;

import com.coachcoach.user.dto.request.OnboardingRequest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

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

    @Length(max = 20, min = 1)
    private String name;
    private Integer employees;
    @Column(scale = 10, precision = 1)
    private BigDecimal laborCost;
    @Column(scale = 15, precision = 2)
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
            BigDecimal rentCost,
            Boolean includeWeeklyHolidayPay
    ) {
        this.name = name;
        this.employees = employees;
        this.laborCost = laborCost;
        this.rentCost = rentCost;
        this.includeWeeklyHolidayPay = includeWeeklyHolidayPay;
        this.updatedAt = LocalDateTime.now();
    }
}