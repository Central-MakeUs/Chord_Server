package com.coachcoach.insight.domain.enums;

import lombok.Getter;

@Getter
public enum HighMarginMenuCompletionPhraseTemplate {
    POSITIVE("고마진 메뉴 집중 판매로 카페 수익 UP!"),
    ;

    private final String completionPhrase;

    HighMarginMenuCompletionPhraseTemplate(String completionPhrase) {
        this.completionPhrase = completionPhrase;
    }
}
