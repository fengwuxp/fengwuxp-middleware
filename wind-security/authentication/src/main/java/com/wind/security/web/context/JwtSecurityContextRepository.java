package com.wind.security.web.context;

import com.wind.common.exception.AssertUtils;
import com.wind.common.exception.BaseException;
import com.wind.security.authentication.jwt.JwtTokenCodec;
import com.wind.security.authentication.jwt.JwtTokenPayload;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.util.StringUtils;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

/**
 * 用于从请求中创建 {@link SecurityContext}
 *
 * @author wuxp
 * @date 2023-10-22 19:10
 **/
@AllArgsConstructor
public class JwtSecurityContextRepository implements SecurityContextRepository {

    private static final String LOGIN_JWT_TOKEN_INVALID = "$.login.jwt.token.invalid";

    private final JwtTokenCodec jwtTokenCodec;

    private final Function<Object, Set<String>> authoritySupplier;

    /**
     * 用户类型
     */
    private final Class<?> userType;

    private final String headerName;

    public JwtSecurityContextRepository(JwtTokenCodec jwtTokenCodec, Class<?> userType) {
        this(jwtTokenCodec, u -> Collections.emptySet(), userType);
    }

    public JwtSecurityContextRepository(JwtTokenCodec jwtTokenCodec, Function<Object, Set<String>> authoritySupplier, Class<?> userType) {
        this(jwtTokenCodec, authoritySupplier, userType, HttpHeaders.AUTHORIZATION);
    }

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        return getSecurityContext(requestResponseHolder.getRequest());
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute(SPRING_SECURITY_CONTEXT_KEY, context);
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        return request.getAttribute(SPRING_SECURITY_CONTEXT_KEY) != null;
    }

    @Override
    public Supplier<SecurityContext> loadContext(HttpServletRequest request) {
        return () -> getSecurityContext(request);
    }

    @Nonnull
    private SecurityContextImpl getSecurityContext(HttpServletRequest request) {
        String jwtToken = request.getHeader(headerName);
        AssertUtils.state(StringUtils.hasLength(jwtToken), () -> BaseException.unAuthorized(LOGIN_JWT_TOKEN_INVALID));
        JwtTokenPayload payload;
        try {
            payload = jwtTokenCodec.parse(jwtToken, userType);
        } catch (Exception e) {
            throw BaseException.unAuthorized(LOGIN_JWT_TOKEN_INVALID);
        }
        // 加载用户权限
        Set<SimpleGrantedAuthority> authorities = authoritySupplier.apply(payload.getUser()).stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(payload.getUser(), jwtToken, authorities);
        return new SecurityContextImpl(authentication);
    }
}
