package com.wind.api.rest.client;

import com.wind.api.rest.util.HttpQueryUtils;
import com.wind.common.exception.AssertUtils;
import com.wind.core.api.signature.ApiSecretAccount;
import com.wind.core.api.signature.DigestSignatureRequest;
import com.wind.core.api.signature.DigestSigner;
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

/**
 * 摘要签名加签请求拦截器
 *
 * @author wuxp
 * @date 2024-02-21 15:45
 * @see https://www.yuque.com/suiyuerufeng-akjad/wind/qal4b72cxw84cu6g
 **/
@Slf4j
public class DigestSignatureRequestInterceptor implements ClientHttpRequestInterceptor {

    /**
     * 需要 requestBody 参与签名的 Content-Type
     */
    private static final List<MediaType> SIGNE_CONTENT_TYPES = Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED);

    private final ApiSecretAccount account;

    private final SignatureHttpHeaderNames headerNames;

    public DigestSignatureRequestInterceptor(ApiSecretAccount account) {
        this(account, null);
    }

    public DigestSignatureRequestInterceptor(ApiSecretAccount account, String headerPrefix) {
        AssertUtils.notNull(account, "argument account must not null");
        this.account = account;
        this.headerNames = new SignatureHttpHeaderNames(headerPrefix);
    }

    @Override
    @NonNull
    public ClientHttpResponse intercept(@NonNull HttpRequest request, @NonNull byte[] body, @NonNull ClientHttpRequestExecution execution) throws IOException {
        DigestSignatureRequest.DigestSignatureRequestBuilder builder = DigestSignatureRequest.builder();
        builder.method(request.getMethodValue())
                .requestPath(request.getURI().getPath())
                .nonce(SequenceGenerator.randomAlphanumeric(32))
                .timestamp(String.valueOf(System.currentTimeMillis()))
                .queryParams(HttpQueryUtils.parseQueryParamsAsMap(request.getURI().getQuery()))
                .secretKey(account.getSecretKey());
        if (signUseRequestBody(request.getHeaders().getContentType())) {
            builder.requestBody(new String(body, StandardCharsets.UTF_8));
        }
        DigestSignatureRequest signatureRequest = builder.build();
        request.getHeaders().add(headerNames.getAccessKey(), account.getAccessKey());
        request.getHeaders().add(headerNames.getTimestamp(), signatureRequest.getTimestamp());
        request.getHeaders().add(headerNames.getNonce(), signatureRequest.getNonce());
        String sign = DigestSigner.SHA256.sign(signatureRequest);
        request.getHeaders().add(headerNames.getSign(), sign);
        if (log.isDebugEnabled()) {
            log.debug("api sign object = {} , sign = {}", request, sign);
        }
        return execution.execute(request, body);
    }

    // TODO 暂时先放到这里，待重构
    public static boolean signUseRequestBody(@Nullable MediaType contentType) {
        if (contentType == null) {
            return false;
        }
        return SIGNE_CONTENT_TYPES.stream().anyMatch(mediaType -> mediaType.includes(contentType));
    }
}
