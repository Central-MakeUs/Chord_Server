package com.coachcoach.app.config;

import com.coachcoach.common.api.UserQueryApi;
import com.coachcoach.common.interceptor.OnboardingCheckInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final String[] INTERCEPTOR_WHITELIST = {
            "/api/v1/auth/**", "api/v1/users/onboarding",
            "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/actuator/**"
    };

    private final UserQueryApi userQueryApi;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOriginPatterns(List.of("http://localhost:*", "http://127.0.0.1:*")); //로컬 개발환경 모두 허용
        cfg.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);
        cfg.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", cfg);
        return src;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new OnboardingCheckInterceptor(userQueryApi))
                .addPathPatterns("/**")
                .excludePathPatterns("/api/v1/auth/**", "/api/v1/users/onboarding");
    }
}
