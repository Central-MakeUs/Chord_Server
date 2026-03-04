package com.coachcoach.insight.domain.enums;

import lombok.Getter;

@Getter
public enum DangerMenuCompletionPhraseTemplate {
    REMOVE_MENU_NEGATIVE("좋은 판단이에요.\n이전보다 더 안정적인 운영 구조에 가까워졌어요."),
    REMOVE_MENU("좋은 판단이에요.\n이번 전략을 적용하면서, {0}의 평균 마진율이 약 {1}%p 개선되었고,\n이 메뉴가 전체 수익성에 미치던 영향도 줄어들었어요."),  // 0: 카페명 / 2: 개선된 평균 마진율(%p)
    ADJUST_PRICE_NEGATIVE("좋은 판단이에요.\n이번 전략을 적용하면서,\n{0}의 원가율이 약 {1}%p 개선되었어요."),   // 0: 메뉴명 / 1: 개선된 평균 원가율
    ADJUST_PRICE("좋은 판단이에요.\n이번 전략을 적용하면서,\n{0}의 평균 마진율이 약 {1}%p 개선되었어요."),   // 0: 카페명 / 1: 개선된 평균 마진율
    ;

    private final String completionPhrase;

    DangerMenuCompletionPhraseTemplate(String completionPhrase) {
        this.completionPhrase = completionPhrase;
    }
}
