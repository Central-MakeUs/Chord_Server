package com.coachcoach.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoUserInfoResponse(
        Long id,

        @JsonProperty("error")
        String error,

        @JsonProperty("error_description")
        String errorDescription
) {
}
