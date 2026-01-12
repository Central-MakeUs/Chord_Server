package com.coachcoach.catalog.api;

import com.coachcoach.catalog.service.request.IngredientCreateRequest;
import com.coachcoach.catalog.service.response.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.FactoryBasedNavigableListAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CatalogApiTest {
    RestClient restClient = RestClient.create("http://localhost:9002");

    @Test
    void readIngredientCategories() {
        List<IngredientCategoryResponse> responses = restClient.get()
                .uri("/ingredient-categories")
                .retrieve()
                .body(new ParameterizedTypeReference<List<IngredientCategoryResponse>>() {});

        System.out.println("ingredient categories");

        for(IngredientCategoryResponse response : responses) {
            System.out.println(response.toString());
        }
    }

    @Test
    void readMenuCategories() {
        List<MenuCategoryResponse> responses = restClient.get()
                .uri("/menu-categories")
                .retrieve()
                .body(new ParameterizedTypeReference<List<MenuCategoryResponse>>() {});

        System.out.println("ingredient categories");

        for(MenuCategoryResponse response : responses) {
            System.out.println(response.toString());
        }
    }

    @Test
    void createIngredient() {
        // given
        IngredientCreateRequest request = new IngredientCreateRequest(
                "MATERIAL",
                "포장용기",
                "EA",
                new BigDecimal("2850"),
                new BigDecimal("100"),
                null
        );

        // when
        IngredientResponse response = restClient.post()
                .uri("/ingredients")
                .body(request)
                .retrieve()
                .body(IngredientResponse.class);  // 응답 받기

        // then
        System.out.println(response.toString());
    }

    @Test
    void readIngredients() {
        List<IngredientResponse> response =
                restClient.get()
                .uri("/ingredients?category=MATERIAL&category=INGREDIENTS")
                .retrieve()
                .body(new ParameterizedTypeReference<List<IngredientResponse>>() {});

        System.out.println("ingredient list");
        response.stream()
                .forEach(System.out::println);
    }

    @Test
    void readIngredientDetail() {
        IngredientDetailResponse response =
                restClient.get()
                        .uri("/ingredients/3")
                        .retrieve()
                        .body(IngredientDetailResponse.class);
        System.out.println("ingredient detail");
        System.out.println(response.toString());
    }

    @Test
    void readIngredientPriceHistory() {
        List<PriceHistoryResponse> responses =
                restClient.get()
                        .uri("/ingredients/3/price-history")
                        .retrieve()
                        .body(new ParameterizedTypeReference<List<PriceHistoryResponse>>() {});

        System.out.println("ingredient price history");

        responses.stream()
                .forEach(System.out::println);
    }

    @Test
    void updateFavorite() {
        restClient.patch()
                .uri("/ingredients/3/favorite?favorite=true")
                .retrieve();
    }

    @Test
    void updateIngredient() {
        IngredientUpdateResponse response = restClient.patch()
                .uri("/ingredients/3")
                .body(new IngredientUpdateRequest(new BigDecimal(9000), new BigDecimal(200), "G"))
                .retrieve()
                .body(IngredientUpdateResponse.class);

        System.out.println("update");
        System.out.println(response.toString());
    }

    @Getter
    @AllArgsConstructor
    static class IngredientCreateRequest {
        @NotBlank(message = "카테고리 입력은 필수입니다.")
        private String categoryCode;        // INGREDIENTS / MATERIAL
        @NotBlank(message = "재료명 입력은 필수입니다.")
        private String ingredientName;
        @NotBlank(message = "단위 입력은 필수입니다.")
        private String unitCode;            // G / KG / EA / ML
        @NotNull(message = "가격 입력은 필수입니다.")
        private BigDecimal price;
        @NotNull(message = "사용량 입력은 필수입니다.")
        private BigDecimal amount;
        private String supplier;
    }

    @ToString
    @Getter
    @AllArgsConstructor
    static class IngredientUpdateRequest {
        @NotNull(message = "가격 입력은 필수입니다.")
        private BigDecimal price;
        @NotNull(message = "사용량 입력은 필수입니다.")
        private BigDecimal amount;
        @NotBlank(message = "단위 입력은 필수입니다.")
        private String unitCode;
    }
}