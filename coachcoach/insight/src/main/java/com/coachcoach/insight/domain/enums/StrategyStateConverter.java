package com.coachcoach.insight.domain.enums;

import com.coachcoach.insight.domain.enums.StrategyState;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;

@Converter(autoApply = true)
public class StrategyStateConverter implements AttributeConverter<StrategyState, String> {

    @Override
    public String convertToDatabaseColumn(StrategyState attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public StrategyState convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;

        return Arrays.stream(StrategyState.values())
                .filter(e -> e.getValue().equals(dbData))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown value: " + dbData));
    }
}