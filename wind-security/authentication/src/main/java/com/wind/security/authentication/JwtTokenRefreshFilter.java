package com.wind.security.authentication;

import com.wind.security.authentication.jwt.JwtTokenCodec;
import lombok.AllArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * jwt token 刷新
 *
 * @author wuxp
 * @date 2023-09-24 16:30
 **/
@AllArgsConstructor
public class JwtTokenRefreshFilter extends OncePerRequestFilter {

    private final JwtTokenCodec jwtTokenCodec;

    @Override
    protected void doFilterInternal(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, javax.servlet.FilterChain chain) throws ServletException, IOException {


    }
}
