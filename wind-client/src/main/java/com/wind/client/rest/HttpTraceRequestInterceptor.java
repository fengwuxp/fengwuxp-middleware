package com.wind.client.rest;

import com.wind.common.WindConstants;
import com.wind.trace.WindTracer;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Collections;

/**
 * 从上线文中设置 traceId 到 http 请求头中
 *
 * @author wuxp
 * @date 2024-01-30 15:37
 **/
public class HttpTraceRequestInterceptor implements ClientHttpRequestInterceptor {

    private final String traceHeaderName;

    public HttpTraceRequestInterceptor(String traceHeaderName) {
        this.traceHeaderName = traceHeaderName;
    }

    public HttpTraceRequestInterceptor() {
        this(WindConstants.WIND_TRANCE_ID_HEADER_NAME);
    }

    @Override
    @NonNull
    public ClientHttpResponse intercept(@NonNull HttpRequest request, @NonNull byte[] body, @NonNull ClientHttpRequestExecution execution) throws IOException {
        String traceId = WindTracer.TRACER.getTraceId();
        if (StringUtils.hasText(traceId)) {
            request.getHeaders().computeIfAbsent(traceHeaderName, k -> Collections.singletonList(traceId));
        }
        return execution.execute(request, body);
    }
}
