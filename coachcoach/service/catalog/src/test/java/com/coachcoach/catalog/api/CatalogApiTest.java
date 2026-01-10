package com.coachcoach.catalog.api;

import com.coachcoach.catalog.service.response.IngredientCategoryResponse;
import com.coachcoach.catalog.service.response.MenuCategoryResponse;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

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
}