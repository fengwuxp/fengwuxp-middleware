package com.wind.client.retrofit;

import com.wind.api.core.signature.ApiSecretAccount;
import com.wind.api.core.signature.ApiSignatureRequest;
import com.wind.api.core.signature.SignatureHttpHeaderNames;
import com.wind.common.exception.AssertUtils;
import com.wind.sequence.SequenceGenerator;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * 接口请求签名加签请求拦截器
 * 参见：https://www.yuque.com/suiyuerufeng-akjad/wind/zl1ygpq3pitl00qp
 *
 * @author wuxp
 * @date 2024-02-26 18:59
 **/
@Slf4j
public class OkHttpApiSignatureRequestInterceptor implements Interceptor {

    /**
     * 需要 requestBody 参与签名的 Content-Type
     */
    private static final List<String> SIGNE_CONTENT_TYPES = Arrays.asList("application/json", "application/x-www-form-urlencoded");

    private final Function<Request, ApiSecretAccount> accountProvider;

    private final SignatureHttpHeaderNames headerNames;

    public OkHttpApiSignatureRequestInterceptor(Function<Request, ApiSecretAccount> accountProvider) {
        this(accountProvider, null);
    }

    public OkHttpApiSignatureRequestInterceptor(Function<Request, ApiSecretAccount> accountProvider, String headerPrefix) {
        AssertUtils.notNull(accountProvider, "argument accountProvider must not null");
        this.accountProvider = accountProvider;
        this.headerNames = new SignatureHttpHeaderNames(headerPrefix);
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        ApiSecretAccount account = accountProvider.apply(request);
        AssertUtils.notNull(account, "ApiSecretAccount must not null");
        ApiSignatureRequest.ApiSignatureRequestBuilder builder = ApiSignatureRequest.builder();
        builder.method(request.method())
                .requestPath(request.url().encodedPath())
                .nonce(SequenceGenerator.randomAlphanumeric(32))
                .timestamp(String.valueOf(System.currentTimeMillis()))
                .queryString(getQueryString(request.url()));
        RequestBody requestBody = request.body();
        if (signRequiredRequestBody(requestBody)) {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            builder.requestBody(new String(buffer.readByteArray(), StandardCharsets.UTF_8));
        }
        ApiSignatureRequest signatureRequest = builder.build();
        Request.Builder requestBuilder = request.newBuilder();
        requestBuilder.addHeader(headerNames.getAccessId(), account.getAccessId());
        requestBuilder.addHeader(headerNames.getTimestamp(), signatureRequest.getTimestamp());
        requestBuilder.addHeader(headerNames.getNonce(), signatureRequest.getNonce());
        String sign = account.getSignAlgorithm().sign(signatureRequest, account.getSecretKey());
        requestBuilder.addHeader(headerNames.getSign(), sign);
        log.debug("api sign object = {} , sign = {}", request, sign);
        Response result = chain.proceed(requestBuilder.build());
        if (result.code() >= 400 && result.code() <= 500) {
            log.debug("sign Debug-Sign-Content = {}", result.headers().get("Debug-Sign-Content"));
            log.debug("sign Debug-Sign-Query = {}", result.headers().get("Debug-Sign-Query"));
        }
        return result;
    }

    private String getQueryString(HttpUrl url) {
        String queryString = url.url().getQuery();
        return StringUtils.hasText(queryString) ? queryString : null;
    }

    private boolean signRequiredRequestBody(RequestBody requestBody) {
        if (requestBody == null) {
            return false;
        }
        MediaType mediaType = requestBody.contentType();
        AssertUtils.notNull(mediaType, "request body content type must not null");
        String[] parts = mediaType.toString().split(";");
        return SIGNE_CONTENT_TYPES.stream().anyMatch(type -> Objects.equals(parts[0], type));
    }
}
