package com.wind.server.web.filters;

import com.wind.server.web.restful.RestfulApiRespFactory;
import com.wind.server.web.supports.ApiResp;
import com.wind.web.utils.HttpResponseMessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Nonnull;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 统一错误捕获
 *
 * @author wuxp
 * @date 2023-10-16 18:15
 **/
@Slf4j
public class RestfulErrorHandleFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull FilterChain chain) {
        try {
            chain.doFilter(request, response);
        } catch (Throwable throwable) {
            if (!response.isCommitted()) {
                logger.error("request error", throwable);
                String message = throwable.getMessage();
                ApiResp<Void> resp = RestfulApiRespFactory.error(StringUtils.hasLength(message) ? message : "unknown error");
                HttpResponseMessageUtils.writeJson(response, resp);
            }
        }
    }
}
