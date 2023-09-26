package com.wind.security.authentication;

import com.wind.security.authentication.jwt.JwtTokenCodec;
import com.wind.security.authentication.jwt.JwtTokenPayload;
import lombok.AllArgsConstructor;
import org.apache.catalina.core.ApplicationFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 从请求头中获取 jwt token ,验证解析通过后设置到请求上下文中 {@link SecurityContextHolder#setContext(SecurityContext)}
 *
 * @author wuxp
 * @date 2023-09-25 09:08
 **/
@AllArgsConstructor
public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenCodec jwtTokenCodec;

    private final Function<String, Set<String>> authoritySupplier;

    /**
     * 用户类型
     */
    private final Class<?> userType;

    private final String headerName;

    public JwtTokenAuthenticationFilter(JwtTokenCodec jwtTokenCodec, Class<?> userType) {
        this(jwtTokenCodec, k -> Collections.emptySet(), userType);
    }

    public JwtTokenAuthenticationFilter(JwtTokenCodec jwtTokenCodec, Function<String, Set<String>> authoritySupplier, Class<?> userType) {
        this(jwtTokenCodec, authoritySupplier, userType, HttpHeaders.AUTHORIZATION);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        if (chain instanceof ApplicationFilterChain || SecurityContextHolder.getContext() != null) {
            // 如果是在tomcat的过滤器链中，不处理
            chain.doFilter(request, response);
            return;
        }
        String jwtToken = request.getHeader(headerName);
        JwtTokenPayload<?> payload = jwtTokenCodec.parse(jwtToken, userType);
        if (payload == null) {
            chain.doFilter(request, response);
            return;
        }
        // 加载用户权限
        Set<SimpleGrantedAuthority> authorities = authoritySupplier.apply(payload.getUserId())
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(payload.getUser(), jwtToken, authorities);
        SecurityContext context = new SecurityContextImpl(authentication);
        SecurityContextHolder.setContext(context);
        chain.doFilter(request, response);
    }
}
