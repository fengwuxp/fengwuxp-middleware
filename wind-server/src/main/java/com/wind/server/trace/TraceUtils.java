package com.wind.server.trace;

import com.google.common.collect.ImmutableMap;
import com.wind.sequence.SequenceGenerator;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static com.wind.common.WindConstants.HTTP_REQUEST_UR_TRACE_NAME;
import static com.wind.common.WindConstants.TRACE_ID_NAME;
import static com.wind.common.WindHttpConstants.HTTP_REQUEST_IP_ATTRIBUTE_NAME;
import static com.wind.common.WindHttpConstants.HTTP_USER_AGENT_HEADER_NAME;

/**
 * trace 工具，暂时使用 {@link MDC} 实现
 *
 * @author wuxp
 * @date 2023-10-18 22:32
 **/
public final class TraceUtils {
    private static final SequenceGenerator TRACE_ID = SequenceGenerator.randomAlphanumeric(32);

    private TraceUtils() {
        throw new AssertionError();
    }

    public static void trace(HttpServletRequest request) {
        String traceId = request.getHeader(TRACE_ID_NAME);
        MDC.put(TRACE_ID_NAME, traceId == null ? TRACE_ID.next() : traceId);
        MDC.put(HTTP_REQUEST_IP_ATTRIBUTE_NAME, (String) request.getAttribute(HTTP_REQUEST_IP_ATTRIBUTE_NAME));
        MDC.put(HTTP_REQUEST_UR_TRACE_NAME, request.getRequestURI());
        MDC.put(HTTP_USER_AGENT_HEADER_NAME, request.getHeader(HttpHeaders.USER_AGENT));
    }

    public static void clearTrace() {
        MDC.clear();
    }

    @Nonnull
    private static Map<String, String> getTraceContext() {
        Map<String, String> context = MDC.getCopyOfContextMap();
        if (context == null) {
            context = ImmutableMap.of(TRACE_ID_NAME, TRACE_ID.next());
            context.forEach(MDC::put);
        }
        return MDC.getCopyOfContextMap();
    }
}
