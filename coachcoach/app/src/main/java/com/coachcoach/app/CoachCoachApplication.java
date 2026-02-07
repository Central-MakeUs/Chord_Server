package com.coachcoach.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(
        scanBasePackages = {
                "com.coachcoach"
        }
)
@EnableCaching
public class CoachCoachApplication {
    public static void main(String[] args) {
        SpringApplication.run(CoachCoachApplication.class, args);
    }
}