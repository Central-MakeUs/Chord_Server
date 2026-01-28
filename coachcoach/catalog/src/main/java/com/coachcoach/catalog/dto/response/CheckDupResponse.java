package com.coachcoach.catalog.dto.response;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class CheckDupResponse {
    private Boolean menuNameDuplicate;
    private List<String> dupIngredientNames;

    public static CheckDupResponse of(Boolean menuNameDuplicate, List<String> dupIngredientNames) {
        CheckDupResponse checkDupResponse = new CheckDupResponse();
        checkDupResponse.menuNameDuplicate = menuNameDuplicate;
        checkDupResponse.dupIngredientNames = dupIngredientNames;
        return checkDupResponse;
    }
}
