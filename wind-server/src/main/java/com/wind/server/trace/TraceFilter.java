package com.wind.server.trace;

import com.wind.trace.http.HttpTraceUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Nonnull;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * trace filter
 *
 * @author wuxp
 * @date 2023-10-18 22:12
 **/
public class TraceFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull FilterChain chain) throws ServletException, IOException {
        HttpTraceUtils.trace(request);
        try {
            chain.doFilter(request, response);
        } finally {
            HttpTraceUtils.clearTrace();
        }
    }

}
