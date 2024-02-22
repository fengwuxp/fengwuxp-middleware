package com.wind.client.rest;

import com.wind.common.WindConstants;
import com.wind.core.api.signature.Sha256WithRsaSigner;
import com.wind.core.api.signature.SignatureHttpHeaderNames;
import com.wind.sequence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * SHA256 with RSA 签名加签请求拦截器
 * 参见：https://www.yuque.com/suiyuerufeng-akjad/wind/uccs7d4vy7au9qg8
 * https://pay.weixin.qq.com/docs/merchant/development/interface-rules/signature-verification.html
 *
 * @author wuxp
 * @date 2024-02-21 18:34
 **/
@Slf4j
@AllArgsConstructor
public class Sha256WithRsaSignatureRequestInterceptor implements ClientHttpRequestInterceptor {

    private static final Set<HttpMethod> HAS_BODY_METHODS = new HashSet<>(Arrays.asList(HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH));

    private final Function<HttpRequest, String> rsaKeyProvider;

    private final SignatureHttpHeaderNames headerNames;

    public Sha256WithRsaSignatureRequestInterceptor(Function<HttpRequest, String> rsaKeyProvider) {
        this(rsaKeyProvider, new SignatureHttpHeaderNames());
    }

    @Override
    @NonNull
    public ClientHttpResponse intercept(@NonNull HttpRequest request, @NonNull byte[] body, @NonNull ClientHttpRequestExecution execution) throws IOException {
        if (HAS_BODY_METHODS.contains(request.getMethod())) {
            String rsaPrivateKey = rsaKeyProvider.apply(request);
            String timestamp = String.valueOf(System.currentTimeMillis());
            String nonce = SequenceGenerator.randomAlphanumeric(32);
            String signText = buildSignText(timestamp, nonce, new String(body, StandardCharsets.UTF_8));
            String sign = Sha256WithRsaSigner.sign(signText, rsaPrivateKey);
            request.getHeaders().add(headerNames.getTimestamp(), timestamp);
            request.getHeaders().add(headerNames.getNonce(), nonce);
            request.getHeaders().add(headerNames.getSign(), sign);
            if (log.isDebugEnabled()) {
                log.debug("SHA256 with RSA sign text = {} , sign = {}", signText, sign);
            }
        }
        return execution.execute(request, body);
    }

    private String buildSignText(String timestamp, String nonce, String body) {
        return timestamp + WindConstants.LF + nonce + WindConstants.LF + body + WindConstants.LF;
    }
}
