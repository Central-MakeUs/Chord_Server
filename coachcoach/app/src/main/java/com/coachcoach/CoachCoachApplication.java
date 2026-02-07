package com.coachcoach;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(
        scanBasePackages = {
                "com.coachcoach"
        }
)
@EnableCaching
@EnableScheduling
public class CoachCoachApplication {
    public static void main(String[] args) {
        SpringApplication.run(CoachCoachApplication.class, args);
    }
}