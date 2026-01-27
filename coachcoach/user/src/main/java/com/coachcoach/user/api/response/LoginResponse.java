package com.coachcoach.user.api.response;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class LoginResponse {
    private String accessToken;

    public static LoginResponse of(String accessToken) {
        LoginResponse loginResponse = new LoginResponse();

        loginResponse.accessToken = accessToken;

        return loginResponse;
    }
}
