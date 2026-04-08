package com.coachcoach.user.dto.response;

import lombok.Getter;

public record NaverUserInfoResponse(
        String resultcode,
        String message,
        Response response
) {
    @Getter
    public static class Response {
        private String id;
    }
}
