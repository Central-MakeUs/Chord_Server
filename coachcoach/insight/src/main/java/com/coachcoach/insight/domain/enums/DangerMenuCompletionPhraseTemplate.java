package com.coachcoach.insight.domain.enums;

import lombok.Getter;

@Getter
public enum DangerMenuCompletionPhraseTemplate {
    REMOVE_MENU("이번 조치로 평균 마진률이 약 {0}%p 감소했지만, 괜찮아요. {1}은 원가 부담이 큰 메뉴였기 때문에, 삭제나 가격 인상 같은 조치가 단기적으로 마진에 영향을 줄 수 있어요. 하지만 장기적으로는 {2}의 수익 구조가 더 안정적으로 변할 거예요. 지금은 조금 아쉽더라도, 건강한 메뉴 구성을 만드는 과정이에요."),  // 0: 메뉴명 / 1: 카페명 / 2: 개선된 평균 마진률(%p)
    ADJUST_PRICE("좋은 판단이에요. 이 조치로 우리 카페의 수익이 증가했어요. 이번 전략을 적용하면서, {0}의 평균 마진율이 약 {1}%p 개선되었어요."),   // 0: 카페명 / 1: 개선된 평균 마진률
    NEGATIVE("이번 조치로 평균 마진률이 약 {0}%p 감소했지만, 괜찮아요. {1}은 원가 부담이 큰 메뉴였기 때문에, 삭제나 가격 인상 같은 조치가 단기적으로 마진에 영향을 줄 수 있어요. 하지만 장기적으로는 {2}의 수익 구조가 더 안정적으로 변할 거예요. 지금은 조금 아쉽더라도, 건강한 메뉴 구성을 만드는 과정이에요."), // 0: 개선된 평균 마진률(절댓값) / 1: 메뉴명 / 2: 카페명
    ;

    private final String completionPhrase;

    DangerMenuCompletionPhraseTemplate(String completionPhrase) {
        this.completionPhrase = completionPhrase;
    }
}
