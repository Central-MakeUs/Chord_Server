package com.coachcoach.insight.domain.enums;

import lombok.Getter;

@Getter
public enum CautionMenuCompletionPhraseTemplate {
    ADJUST_PRICE_NEGATIVE("좋은 판단이에요.\n이번 전략을 적용하면서, {0}의 평균 원가율이 약 {1}%p 개선되었어요."),     // 0: 메뉴명, 1: 개선된 평균 원가율
    ADJUST_PRICE("좋은 선택이에요.\n이번 전략을 적용하면서,\n{0}의 평균 마진율이 약 {1}%p 개선되었어요."),    // 0: 카페명 / 1: 개선된 평균 마진률
    ADJUST_RECIPE_NEGATIVE("좋은 판단이에요.\n이전보다 더 안정적인 운영 구조에 가까워졌고,\n공헌이익이 {0}원 증가했어요."),      // 0: 안정단계 원가율 적용시 공헌이익 증가액
    ADJUST_RECIPE("좋은 선택이에요.\n이번 전략을 적용하면서,\n{0}의 평균 마진율이 약 {1}%p 개선되었어요."),         // 0: 카페명 / 1: 개선된 평균 마진률
    ;

    private final String completionPhrase;

    CautionMenuCompletionPhraseTemplate(String completionPhrase) {
        this.completionPhrase = completionPhrase;
    }
}
