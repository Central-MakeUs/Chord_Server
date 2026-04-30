package com.coachcoach.user.dto.response;

import lombok.Getter;

import java.util.List;

public record ApplePublicKeyResponse(
        List<ApplePublicKey> keys
) {
    @Getter
    public static class ApplePublicKey {
        private String kty;
        private String kid;
        private String use;
        private String alg;
        private String n;   // RSA modulus
        private String e;   // RSA exponent
    }
}
