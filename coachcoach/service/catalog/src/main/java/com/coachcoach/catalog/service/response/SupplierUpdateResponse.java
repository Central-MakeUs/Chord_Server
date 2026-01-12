package com.coachcoach.catalog.service.response;

import com.coachcoach.catalog.entity.Ingredient;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SupplierUpdateResponse {
    private String supplier;

    public static SupplierUpdateResponse from(Ingredient ingredient) {
        SupplierUpdateResponse response = new SupplierUpdateResponse();

        response.supplier = ingredient.getSupplier();

        return response;
    }
}
