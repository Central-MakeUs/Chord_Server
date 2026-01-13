package com.coachcoach.catalog.global.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                "ingredient-categories",  // 재료 카테고리 엔티티
                "menu-categories",          // 메뉴 카테고리 엔티티
                "unit-code",                // 단위 코드 엔티티
                "margin-grade"              // 마진 등급 엔티티
        );

        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(7, TimeUnit.DAYS)
                .recordStats());

        return cacheManager;
    }
}
