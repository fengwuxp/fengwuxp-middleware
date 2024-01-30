package com.wind.web.client;

import com.wind.common.WindConstants;
import com.wind.trace.WindTracer;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.util.Collections;

/**
 * 从上线文中设置 traceId 到 http 请求头中
 *
 * @author wuxp
 * @date 2024-01-30 15:37
 **/
public class ClientHttpTraceRequestInterceptor implements ClientHttpRequestInterceptor {

    private final String traceHeaderName;

    public ClientHttpTraceRequestInterceptor(String traceHeaderName) {
        this.traceHeaderName = traceHeaderName;
    }

    public ClientHttpTraceRequestInterceptor() {
        this(WindConstants.WIND_TRANCE_ID_HEADER_NAME);
    }

    @Override
    @NonNull
    public ClientHttpResponse intercept(@NonNull HttpRequest request, @NonNull byte[] body, @NonNull ClientHttpRequestExecution execution) throws IOException {
        String traceId = WindTracer.TRACER.getTraceId();
        if (traceId != null) {
            request.getHeaders().computeIfAbsent(traceHeaderName, k -> Collections.singletonList(traceId));
        }
        return execution.execute(request, body);
    }
}
