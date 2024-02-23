package com.wind.core.api.signature;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class ApiSignatureRequestTest {

    @Test
    void testTetSignText() {
        ApiSignatureRequest request = buildRequest("a=1&b=2&c=b,cd", "{}");
        Assertions.assertEquals("method=POST&requestPath=/api/v1/example/users&nonce=jlj3rn2930d-123210dq&timestamp=123456789&queryStringMd5=8a1e3c93105e6af6494526bd28a55a65&requestBodyMd5=99914b932bd37a50b983c5e7c90ae93b", request.getSignTextForDigest());
    }

    @Test
    void testTetSignTextNoneQueryString() {
        ApiSignatureRequest request = buildRequest(null, "{}");
        Assertions.assertEquals("method=POST&requestPath=/api/v1/example/users&nonce=jlj3rn2930d-123210dq&timestamp=123456789&requestBodyMd5=99914b932bd37a50b983c5e7c90ae93b", request.getSignTextForDigest());
    }

    @Test
    void testTetSignTextNoneRequestBody() {
        ApiSignatureRequest request = buildRequest("a=1&b=2&c=b,cd", null);
        Assertions.assertEquals("method=GET&requestPath=/api/v1/example/users&nonce=jlj3rn2930d-123210dq&timestamp=123456789&queryStringMd5=8a1e3c93105e6af6494526bd28a55a65", request.getSignTextForDigest());
    }

    @Test
    void testCanonicalizedQueryString() {
        Map<String, String[]> queryParams = new HashMap<>();
        queryParams.put("name", new String[]{"zhans"});
        queryParams.put("age", new String[]{"36"});
        queryParams.put("tags", new String[]{"tag0", "tag1"});
        ApiSignatureRequest request = buildRequestByQueryParams(queryParams, "{}");
        Assertions.assertEquals("age=36&name=zhans&tags=tag0&tags=tag1", request.getCanonicalizedQueryString());
        Assertions.assertEquals("method=POST&requestPath=/api/v1/example/users&nonce=jlj3rn2930d-123210dq&timestamp=123456789&queryStringMd5=7e7a72e5a7da742a9586a70c06b98322&requestBodyMd5=99914b932bd37a50b983c5e7c90ae93b", request.getSignTextForDigest());
    }

    private static ApiSignatureRequest buildRequest(String queryString, String requestBody) {
        return ApiSignatureRequest.builder()
                .method(requestBody == null ? "GET" : "POST")
                .requestPath("/api/v1/example/users")
                .queryString(queryString)
                .requestBody(requestBody)
                .timestamp("123456789")
                .nonce("jlj3rn2930d-123210dq")
                .secretKey("test")
                .build();
    }

    private static ApiSignatureRequest buildRequestByQueryParams(Map<String, String[]> queryParams, String requestBody) {
        return ApiSignatureRequest.builder()
                .method(requestBody == null ? "GET" : "POST")
                .requestPath("/api/v1/example/users")
                .queryParams(queryParams)
                .requestBody(requestBody)
                .timestamp("123456789")
                .nonce("jlj3rn2930d-123210dq")
                .secretKey("test")
                .build();
    }
}