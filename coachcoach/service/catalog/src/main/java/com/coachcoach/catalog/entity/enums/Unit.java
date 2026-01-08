package com.coachcoach.catalog.entity.enums;

import com.coachcoach.catalog.entity.converter.AbstractCodedEnumConverter;
import com.coachcoach.catalog.entity.converter.CodedEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Unit implements CodedEnum<String> {
    G("G", 100),
    KG("KG", 1),
    EA("EA", 1),
    ML("ML", 100),
    ;

    private final String code;
    private final Integer baseQuantity;

    @jakarta.persistence.Converter(autoApply = true)
    static class Converter extends AbstractCodedEnumConverter<Unit, String> {
        public Converter() {
            super(Unit.class);
        }
    }
}
