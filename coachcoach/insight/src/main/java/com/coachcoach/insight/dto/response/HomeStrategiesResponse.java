package com.coachcoach.insight.dto.response;

import java.util.List;

public record HomeStrategiesResponse(
        List<HomeStrategyBrief> strategies
) {
}
