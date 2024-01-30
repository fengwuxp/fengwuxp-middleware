package com.wind.server.trace;

import com.google.common.collect.ImmutableSet;
import com.wind.common.WindConstants;
import com.wind.common.utils.IpAddressUtils;
import com.wind.common.utils.ServiceInfoUtils;
import com.wind.server.web.restful.RestfulApiRespFactory;
import com.wind.server.web.supports.ApiResp;
import com.wind.trace.WindTracer;
import com.wind.web.utils.HttpResponseMessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Nonnull;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.wind.common.WindConstants.HTTP_REQUEST_UR_TRACE_NAME;
import static com.wind.common.WindConstants.LOCAL_HOST_IP_V4;
import static com.wind.common.WindConstants.WIND_TRANCE_ID_HEADER_NAME;
import static com.wind.common.WindHttpConstants.HTTP_REQUEST_IP_ATTRIBUTE_NAME;
import static com.wind.common.WindHttpConstants.HTTP_USER_AGENT_HEADER_NAME;

/**
 * trace filter
 *
 * @author wuxp
 * @date 2023-10-18 22:12
 **/
@Slf4j
public class TraceFilter extends OncePerRequestFilter {

    /**
     * 线下环境给 client 响应真实的服务 IP
     */
    private static final String REAL_SERVER_IP = "Real-Server-Ip";

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

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull FilterChain chain) throws ServletException, IOException {
        String traceId = trace(request);
        try {
            if (!ServiceInfoUtils.isOnline()) {
                // 线下环境增加服务端 ip 返回
                response.setHeader(REAL_SERVER_IP, IpAddressUtils.getLocalIpv4());
            }
            // 提前写入 traceId 到响应头，避免 response committed 后
            response.setHeader(WIND_TRANCE_ID_HEADER_NAME, traceId);
            chain.doFilter(request, response);
        } catch (Throwable throwable) {
            // 统一错误捕获
            log.error("request error", throwable);
            ApiResp<Void> resp = RestfulApiRespFactory.withThrowable(throwable);
            HttpResponseMessageUtils.writeApiResp(response, resp);
        } finally {
            WindTracer.TRACER.clearTraceContext();
        }
    }

    private String trace(HttpServletRequest request) {
        String traceId = request.getHeader(WIND_TRANCE_ID_HEADER_NAME);
        Map<String, Object> contextVariables = new HashMap<>();
        // 将用户请求来源 ip 并设置到请求上下文中
        String requestSourceIp = getRequestSourceIp(request);
        request.setAttribute(HTTP_REQUEST_IP_ATTRIBUTE_NAME, requestSourceIp);
        contextVariables.put(HTTP_REQUEST_IP_ATTRIBUTE_NAME, requestSourceIp);
        contextVariables.put(HTTP_REQUEST_UR_TRACE_NAME, request.getRequestURI());
        contextVariables.put(HTTP_USER_AGENT_HEADER_NAME, request.getHeader(HttpHeaders.USER_AGENT));
        contextVariables.put(LOCAL_HOST_IP_V4, IpAddressUtils.getLocalIpv4());
        WindTracer.TRACER.trace(traceId, contextVariables);
        return  WindTracer.TRACER.getTraceId();
    }

    /**
     * 获取请求来源 IP 地址, 如果通过代理进来，则透过防火墙获取真实IP地址
     *
     * @param request 请求对象
     * @return 真实 ip
     */
    private String getRequestSourceIp(HttpServletRequest request) {
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

}
