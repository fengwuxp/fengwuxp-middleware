package com.wind.server.web.filters;

import com.google.common.collect.ImmutableSet;
import com.wind.common.utils.IpAddressUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import static com.wind.server.web.WindWebConstants.HTTP_REQUEST_IP_ATTR_NAME;

/**
 * 获取用户请求来源 ip 并设置到请求上下文中
 *
 * @author wuxp
 * @date 2023-09-23 07:07
 **/
public class RequestSourceIpFilter extends OncePerRequestFilter {

    private static final Set<String> IP_HEAD_NAME_LIST = ImmutableSet.copyOf(
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
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        request.setAttribute(HTTP_REQUEST_IP_ATTR_NAME, getRequestSourceIp(request));
        chain.doFilter(request, response);
    }

    /**
     * 获取请求来源 IP 地址, 如果通过代理进来，则透过防火墙获取真实IP地址
     *
     * @param request 请求对象
     * @return 真实 ip
     */
    public String getRequestSourceIp(HttpServletRequest request) {
        for (String headName : IP_HEAD_NAME_LIST) {
            String ip = request.getHeader(headName);
            if (ip == null || ip.trim().isEmpty()) {
                continue;
            }
            ip = ip.trim();
            // 对于通过多个代理的情况， 第一个 ip 为客户端真实IP,多个IP按照 ',' 分隔
            String[] sections = ip.split(",");
            for (String section : sections) {
                if (IpAddressUtils.isValidIp(section)) {
                    return section;
                }
            }
        }
        return request.getRemoteAddr();
    }

}
