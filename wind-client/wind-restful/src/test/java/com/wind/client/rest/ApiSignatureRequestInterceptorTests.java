package com.wind.client.rest;

import com.wind.core.api.signature.ApiSecretAccount;
import com.wind.core.api.signature.SignatureHttpHeaderNames;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * @author wuxp
 * @date 2024-02-21 17:30
 **/
class ApiSignatureRequestInterceptorTests {

    @Test
    void testSha256() throws IOException {
        ApiSignatureRequestInterceptor interceptor = new ApiSignatureRequestInterceptor(httpRequest -> ApiSecretAccount.immutable(RandomStringUtils.randomAlphabetic(12), RandomStringUtils.randomAlphabetic(32)));
        ClientHttpResponse response = interceptor.intercept(mockHttpRequest(), new byte[0], mockExecution());
        HttpHeaders headers = response.getHeaders();
        String sign = new SignatureHttpHeaderNames(null).getSign();
        Assertions.assertNotNull(headers.get(sign));
    }

    private static ClientHttpRequestExecution mockExecution() {
        return (request, body) -> new ClientHttpResponse() {
            @Override
            public HttpStatus getStatusCode() {
                return HttpStatus.OK;
            }

            @Override
            public int getRawStatusCode() {
                return HttpStatus.OK.value();
            }

            @Override
            public String getStatusText() {
                return HttpStatus.OK.getReasonPhrase();
            }

            @Override
            public void close() {

            }

            @Override
            public InputStream getBody() throws IOException {
                return new ByteArrayInputStream("success".getBytes(StandardCharsets.UTF_8));
            }

            @Override
            public HttpHeaders getHeaders() {
                return request.getHeaders();
            }
        };
    }

    private HttpRequest mockHttpRequest() {
        HttpHeaders httpHeaders = new HttpHeaders();
        return new HttpRequest() {
            @Override
            public String getMethodValue() {
                return HttpMethod.GET.name();
            }

            @Override
            public URI getURI() {
                return URI.create("/api/v1/user/1");
            }

            @Override
            public HttpHeaders getHeaders() {
                return httpHeaders;
            }
        };
    }

}
