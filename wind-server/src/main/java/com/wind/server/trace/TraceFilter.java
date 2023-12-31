package com.wind.server.trace;

import com.wind.common.utils.ServiceInfoUtils;
import com.wind.server.web.restful.RestfulApiRespFactory;
import com.wind.server.web.supports.ApiResp;
import com.wind.trace.http.HttpTraceUtils;
import com.wind.web.utils.HttpResponseMessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Nonnull;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.wind.common.WindConstants.WIND_TRANCE_ID_HEADER_NAME;
import static com.wind.common.WindHttpConstants.HTTP_REQUEST_IP_ATTRIBUTE_NAME;

/**
 * trace filter
 *
 * @author wuxp
 * @date 2023-10-18 22:12
 **/
@Slf4j
public class TraceFilter extends OncePerRequestFilter {

    private static final String REAL_SERVER_IP = "Real-Server-Ip";

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull FilterChain chain) throws ServletException, IOException {
        // 将用户请求来源 ip 并设置到请求上下文中
        request.setAttribute(HTTP_REQUEST_IP_ATTRIBUTE_NAME, HttpTraceUtils.getRequestSourceIp(request));
        HttpTraceUtils.trace(request);
        try {
            if (!ServiceInfoUtils.isOnline()) {
                // 线下环境增加服务端 ip 返回
                response.setHeader(REAL_SERVER_IP, HttpTraceUtils.getLocalIpv4());
            }
            // 提前写入 traceId 到响应头，避免 response committed 后
            response.setHeader(WIND_TRANCE_ID_HEADER_NAME, HttpTraceUtils.getTraceContext().getTraceId());
            chain.doFilter(request, response);
        } catch (Throwable throwable) {
            // 统一错误捕获
            log.error("request error", throwable);
            ApiResp<Void> resp = RestfulApiRespFactory.withThrowable(throwable);
            HttpResponseMessageUtils.writeApiResp(response, resp);
        } finally {
            HttpTraceUtils.clearTrace();
        }
    }

}
