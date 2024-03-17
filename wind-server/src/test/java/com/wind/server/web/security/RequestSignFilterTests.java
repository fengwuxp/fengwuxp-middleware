package com.wind.server.web.security;

import com.wind.client.rest.ApiSignatureRequestInterceptor;
import com.wind.common.WindConstants;
import com.wind.common.WindHttpConstants;
import com.wind.core.api.signature.ApiSecretAccount;
import com.wind.core.api.signature.ApiSignAlgorithm;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.util.ObjectUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

/**
 * @author wuxp
 * @date 2024-03-04 13:14
 **/
class RequestSignFilterTests {

    private final ApiSecretAccount secretAccount = ApiSecretAccount.immutable(RandomStringUtils.randomAlphabetic(12), RandomStringUtils.randomAlphabetic(32), ApiSignAlgorithm.HMAC_SHA256);

    private RequestSignFilter signFilter;

    @BeforeEach
    void setup() {
        System.setProperty(WindConstants.SPRING_PROFILES_ACTIVE, WindConstants.DEV);
        signFilter = new RequestSignFilter((accessId, secretVersion) -> secretAccount, Collections.emptyList(), true);
    }

    @Test
    void testSignError() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/examples");
        MockHttpServletResponse response = new MockHttpServletResponse();
        signFilter.doFilter(request, response, new MockFilterChain());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    void testSignSuccess() throws Exception {
        UriComponents uriComponents = UriComponentsBuilder.fromUriString("https://www.example.com/api/v1/examples?a=2&b=20&name=张三").build();
        MockHttpServletRequest request = new MockHttpServletRequest("POST", uriComponents.getPath());
        uriComponents.getQueryParams().forEach((name, values) -> {
            if (!ObjectUtils.isEmpty(values)) {
                request.setParameter(name, values.get(0));
            }
        });
        byte[] requestBody = RandomStringUtils.randomAlphabetic(1000).getBytes(StandardCharsets.UTF_8);
        request.setContent(requestBody);
        ApiSignatureRequestInterceptor interceptor = new ApiSignatureRequestInterceptor(httpRequest -> secretAccount);
        interceptor.intercept(new ServletServerHttpRequest(request), requestBody, (r, body) -> {
            r.getHeaders().forEach((name, values) -> {
                if (!ObjectUtils.isEmpty(values)) {
                    request.addHeader(name, values.get(0));
                }
            });
            return new MockClientHttpResponse(new byte[0], 200);
        });
        MockHttpServletResponse response = new MockHttpServletResponse();
        signFilter.doFilter(request, response, new MockFilterChain());
        Assertions.assertNotNull(request.getAttribute(WindHttpConstants.API_SECRET_ACCOUNT_ATTRIBUTE_NAME));
    }
}
