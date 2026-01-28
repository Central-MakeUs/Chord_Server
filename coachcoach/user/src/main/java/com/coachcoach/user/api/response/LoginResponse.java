package com.coachcoach.user.api.response;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class LoginResponse {
    private String accessToken;
    private String refreshToken;

    public static LoginResponse of(String accessToken, String refreshToken) {
        LoginResponse loginResponse = new LoginResponse();

        loginResponse.accessToken = accessToken;
        loginResponse.refreshToken = refreshToken;

        return loginResponse;
    }
}
