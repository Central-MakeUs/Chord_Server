package com.coachcoach.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoAccessTokenValidateResponse(
        Long id,

        @JsonProperty("expires_in")
        Integer expiresIn,

        @JsonProperty("app_id")
        Integer appId
) {
}
