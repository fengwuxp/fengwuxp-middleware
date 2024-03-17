package com.wind.client.rest;

import com.wind.common.exception.AssertUtils;
import com.wind.core.api.signature.ApiSecretAccount;
import com.wind.core.api.signature.ApiSignatureRequest;
import com.wind.core.api.signature.SignatureHttpHeaderNames;
import com.wind.sequence.SequenceGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * 接口请求签名加签请求拦截器
 * 参见：https://www.yuque.com/suiyuerufeng-akjad/wind/zl1ygpq3pitl00qp
 *
 * @author wuxp
 * @date 2024-02-21 15:45
 **/
@Slf4j
public class ApiSignatureRequestInterceptor implements ClientHttpRequestInterceptor {

    /**
     * 需要 requestBody 参与签名的 Content-Type
     */
    private static final List<MediaType> SIGNE_CONTENT_TYPES = Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED);

    private final Function<HttpRequest, ApiSecretAccount> accountProvider;

    private final SignatureHttpHeaderNames headerNames;

    public ApiSignatureRequestInterceptor(Function<HttpRequest, ApiSecretAccount> accountProvider) {
        this(accountProvider, null);
    }

    public ApiSignatureRequestInterceptor(Function<HttpRequest, ApiSecretAccount> accountProvider, String headerPrefix) {
        AssertUtils.notNull(accountProvider, "argument accountProvider must not null");
        this.accountProvider = accountProvider;
        this.headerNames = new SignatureHttpHeaderNames(headerPrefix);
    }

    @Override
    @NonNull
    public ClientHttpResponse intercept(@NonNull HttpRequest request, @NonNull byte[] body, @NonNull ClientHttpRequestExecution execution) throws IOException {
        ApiSecretAccount account = accountProvider.apply(request);
        AssertUtils.notNull(account, "ApiSecretAccount must not null");
        ApiSignatureRequest.ApiSignatureRequestBuilder builder = ApiSignatureRequest.builder();
        builder.method(request.getMethodValue())
                .requestPath(request.getURI().getPath())
                .nonce(SequenceGenerator.randomAlphanumeric(32))
                .timestamp(String.valueOf(System.currentTimeMillis()))
                .queryString(request.getURI().getQuery());
        if (signRequiredRequestBody(request.getHeaders().getContentType())) {
            builder.requestBody(new String(body, StandardCharsets.UTF_8));
        }
        ApiSignatureRequest signatureRequest = builder.build();
        request.getHeaders().add(headerNames.getAccessId(), account.getAccessId());
        request.getHeaders().add(headerNames.getTimestamp(), signatureRequest.getTimestamp());
        request.getHeaders().add(headerNames.getNonce(), signatureRequest.getNonce());
        String sign = account.getSignAlgorithm().sign(signatureRequest, account.getSecretKey());
        request.getHeaders().add(headerNames.getSign(), sign);
        if (log.isDebugEnabled()) {
            log.debug("api sign object = {} , sign = {}", request, sign);
        }
        return execution.execute(request, body);
    }

    // TODO 暂时先放到这里，待重构
    public static boolean signRequiredRequestBody(@Nullable MediaType contentType) {
        if (contentType == null) {
            return false;
        }
        return SIGNE_CONTENT_TYPES.stream().anyMatch(mediaType -> mediaType.includes(contentType));
    }
}
