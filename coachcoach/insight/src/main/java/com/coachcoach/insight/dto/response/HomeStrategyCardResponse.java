package com.coachcoach.insight.dto.response;

import java.util.List;

public record HomeStrategyCardResponse(
        Integer numOfDangerMenus,
        List<DangerMenuBriefCard> dangerMenuCards
) {
}
