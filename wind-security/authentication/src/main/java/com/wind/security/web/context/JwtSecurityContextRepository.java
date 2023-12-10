package com.wind.security.web.context;

import com.wind.common.exception.BaseException;
import com.wind.security.authentication.jwt.JwtToken;
import com.wind.security.authentication.jwt.JwtTokenCodec;
import com.wind.security.authentication.jwt.JwtUser;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.wind.security.WebSecurityConstants.LOGIN_JWT_TOKEN_INVALID;
import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

/**
 * 用于从请求中创建 {@link SecurityContext}
 *
 * @author wuxp
 * @date 2023-10-22 19:10
 **/
@AllArgsConstructor
public class JwtSecurityContextRepository implements SecurityContextRepository {

    private static final SecurityContext EMPTY = new SecurityContextImpl();

    private final JwtTokenCodec jwtTokenCodec;

    /**
     * 权限加载器
     */
    private final Function<JwtUser, Set<String>> authoritySupplier;

    /**
     * jwt token 请求头名称
     */
    private final String headerName;

    public JwtSecurityContextRepository(JwtTokenCodec jwtTokenCodec, Function<JwtUser, Set<String>> authoritySupplier) {
        this(jwtTokenCodec, authoritySupplier, HttpHeaders.AUTHORIZATION);
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
    private SecurityContext getSecurityContext(HttpServletRequest request) {
        String jwtToken = request.getHeader(headerName);
        JwtToken payload;
        try {
            payload = jwtTokenCodec.parse(jwtToken);
        } catch (Exception e) {
            throw BaseException.unAuthorized(LOGIN_JWT_TOKEN_INVALID);
        }
        if (payload == null) {
            return EMPTY;
        }
        // 加载用户权限
        Set<SimpleGrantedAuthority> authorities = authoritySupplier.apply(payload.getUser()).stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
        Authentication authentication = new JwtAuthenticationToken(payload.getUser(), jwtToken, authorities);
        return new SecurityContextImpl(authentication);
    }
}
