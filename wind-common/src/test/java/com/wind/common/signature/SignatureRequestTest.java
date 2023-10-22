package com.wind.common.signature;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SignatureRequestTest {

    @Test
    void testTetSignText() {
        SignatureRequest request = buildRequest("a=1&b=2&c=b,cd","{}");
        Assertions.assertEquals("method=GET&requestPath=/api/v1/example/users&nonce=jlj3rn2930d-123210dq&timestamp=123456789&queryString=8a1e3c93105e6af6494526bd28a55a65&requestBody=99914b932bd37a50b983c5e7c90ae93b",request.getSignText());
    }

    @Test
    void testTetSignTextNoneQueryString() {
        SignatureRequest request = buildRequest(null,"{}");
        Assertions.assertEquals("method=GET&requestPath=/api/v1/example/users&nonce=jlj3rn2930d-123210dq&timestamp=123456789&requestBody=99914b932bd37a50b983c5e7c90ae93b",request.getSignText());
    }

    @Test
    void testTetSignTextNoneRequestBody() {
        SignatureRequest request = buildRequest("a=1&b=2&c=b,cd",null);
        Assertions.assertEquals("method=GET&requestPath=/api/v1/example/users&nonce=jlj3rn2930d-123210dq&timestamp=123456789&queryString=8a1e3c93105e6af6494526bd28a55a65",request.getSignText());
    }

    private static SignatureRequest buildRequest(String queryString,String requestBody) {
        return SignatureRequest.builder()
                .method("GET")
                .requestPath("/api/v1/example/users")
                .queryString(queryString)
                .requestBody(requestBody)
                .timestamp("123456789")
                .nonce("jlj3rn2930d-123210dq")
                .secretKey("test")
                .build();
    }
}