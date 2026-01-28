package com.coachcoach.catalog.dto.response;

import java.util.List;

public record CheckDupResponse (
    Boolean menuNameDuplicate,
    List<String> dupIngredientNames
) { }
