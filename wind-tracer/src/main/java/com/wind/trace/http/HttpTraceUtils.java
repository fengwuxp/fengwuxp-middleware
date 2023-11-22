package com.wind.trace.http;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.wind.common.WindConstants;
import com.wind.common.utils.IpAddressUtils;
import com.wind.sequence.SequenceGenerator;
import com.wind.trace.WindTraceContext;
import com.wind.web.utils.HttpServletRequestUtils;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import static com.wind.common.WindConstants.HTTP_REQUEST_UR_TRACE_NAME;
import static com.wind.common.WindConstants.LOCAL_HOST_IP_V4;
import static com.wind.common.WindConstants.TRACE_ID_NAME;
import static com.wind.common.WindConstants.WIND_TRANCE_ID_HEADER_NAME;
import static com.wind.common.WindHttpConstants.HTTP_REQUEST_IP_ATTRIBUTE_NAME;
import static com.wind.common.WindHttpConstants.HTTP_USER_AGENT_HEADER_NAME;

/**
 * http trace 工具，暂时使用 {@link MDC} 实现
 *
 * @author wuxp
 * @date 2023-10-18 22:32
 **/
public final class HttpTraceUtils {

    /**
     * ip 头名称
     */
    private static final Set<String> IP_HEAD_NAMES = ImmutableSet.copyOf(
            Arrays.asList(
                    "X-Real-IP",
                    "X-Forwarded-For",
                    "Proxy-Client-IP",
                    "WL-Proxy-Client-IP",
                    "REMOTE-HOST",
                    "HTTP_CLIENT_IP",
                    "HTTP_X_FORWARDED_FOR")
    );

    private static final SequenceGenerator TRACE_ID = () -> SequenceGenerator.randomAlphanumeric(32);

    private HttpTraceUtils() {
        throw new AssertionError();
    }

    public static void trace(HttpServletRequest request) {
        String traceId = request.getHeader(WIND_TRANCE_ID_HEADER_NAME);
        MDC.put(TRACE_ID_NAME, traceId == null ? TRACE_ID.next() : traceId);
        MDC.put(HTTP_REQUEST_IP_ATTRIBUTE_NAME, (String) request.getAttribute(HTTP_REQUEST_IP_ATTRIBUTE_NAME));
        MDC.put(HTTP_REQUEST_UR_TRACE_NAME, request.getRequestURI());
        MDC.put(HTTP_USER_AGENT_HEADER_NAME, request.getHeader(HttpHeaders.USER_AGENT));
        MDC.put(LOCAL_HOST_IP_V4, IpAddressUtils.getLocalIpv4());
    }

    public static void clearTrace() {
        MDC.clear();
    }

    @Nonnull
    public static WindTraceContext getTraceContext() {
        Map<String, String> mdc = getMdcContext();
        return new WindTraceContext() {

            @Nonnull
            @Override
            public String getTraceId() {
                String traceId = getContextVariable(TRACE_ID_NAME);
                if (traceId == null) {
                    traceId = HttpServletRequestUtils.requiredContextRequest().getHeader(TRACE_ID_NAME);
                    if (traceId == null) {
                        // 没有则生成
                        traceId = TRACE_ID.next();
                    }
                    MDC.put(TRACE_ID_NAME, traceId);
                }
                return traceId;
            }

            @Override
            public Map<String, String> asContextVariables() {
                return mdc;
            }

            @Override
            public String getContextVariable(String variableName) {
                return mdc.get(variableName);
            }
        };
    }

    @Nonnull
    private static Map<String, String> getMdcContext() {
        Map<String, String> context = MDC.getCopyOfContextMap();
        if (context == null) {
            context = ImmutableMap.of(TRACE_ID_NAME, TRACE_ID.next());
            context.forEach(MDC::put);
        }
        return context;
    }

    /**
     * 获取请求来源 IP 地址, 如果通过代理进来，则透过防火墙获取真实IP地址
     *
     * @param request 请求对象
     * @return 真实 ip
     */
    @NonNull
    public static String getRequestSourceIp(HttpServletRequest request) {
        for (String headName : IP_HEAD_NAMES) {
            String ip = request.getHeader(headName);
            if (ip == null || ip.trim().isEmpty()) {
                continue;
            }
            ip = ip.trim();
            // 对于通过多个代理的情况， 第一个 ip 为客户端真实IP,多个IP按照 ',' 分隔
            String[] sections = ip.split(WindConstants.COMMA);
            for (String section : sections) {
                if (IpAddressUtils.isValidIp(section)) {
                    return section;
                }
            }
        }
        return request.getRemoteAddr();
    }

    @Nullable
    public static String getLocalIpv4() {
        return MDC.get(LOCAL_HOST_IP_V4);
    }
}
