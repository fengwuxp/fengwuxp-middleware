package com.wind.server.web.security.signature;


import com.google.common.collect.ImmutableSet;
import com.wind.common.signature.SignatureRequest;
import com.wind.common.signature.Signer;
import com.wind.common.utils.ServiceInfoUtils;
import com.wind.server.servlet.RepeatableReadRequestWrapper;
import com.wind.server.web.filters.WindWebFilterOrdered;
import com.wind.server.web.restful.RestfulApiRespFactory;
import com.wind.web.utils.HttpResponseMessageUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Set;
import java.util.function.UnaryOperator;

/**
 * 接口请求验签
 * 参见文档：https://www.yuque.com/suiyuerufeng-akjad/wind/zvhdgh1voqxa0ly1
 *
 * @author wuxp
 */
@Slf4j
@AllArgsConstructor
public class RequestSignFilter implements Filter, Ordered {

    /**
     * 需要 requestBody 参与签名的 content-type
     */
    private static final Set<MediaType> SIGNE_CONTENT_TYPES = ImmutableSet.of(MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED);

    private final SignatureHttpHeaderNames headerNames;

    private final UnaryOperator<String> secretKeyProvider;

    /**
     * 忽略接口验签的请求匹配器
     */
    private final Collection<RequestMatcher> ignoreRequestMatchers;

    /**
     * 是否启用
     */
    private final boolean enable;

    public RequestSignFilter(UnaryOperator<String> secretKeyProvider, Collection<RequestMatcher> ignoreRequestMatchers, boolean enable) {
        this(new SignatureHttpHeaderNames(), secretKeyProvider, ignoreRequestMatchers, enable);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        if (!enable || ignoreSignCheck(request)) {
            log.debug("request no signature required, enable = {}", false);
            chain.doFilter(servletRequest, servletResponse);
            return;
        }
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String accessKey = request.getHeader(headerNames.accessKey);
        if (!StringUtils.hasLength(accessKey)) {
            badRequest(response, "request ak must not empty");
            return;
        }
        RequestSignMatcher signMatcher = new RequestSignMatcher(request);
        SignatureRequest signatureRequest = buildSignatureRequest(accessKey, signMatcher);
        String requestSign = request.getHeader(headerNames.sign);
        if (Signer.SHA256.verify(requestSign, signatureRequest)) {
            chain.doFilter(signMatcher.request, servletResponse);
        } else {
            if (!ServiceInfoUtils.isOnline()) {
                // 线下环境返回服务端的签名，用于 debug
                response.addHeader(headerNames.debugSign, Signer.SHA256.sign(signatureRequest));
                response.addHeader(headerNames.debugSignContent, signatureRequest.getSignText());
            }
            badRequest(response, "sign verify error");
        }
    }

    private void badRequest(HttpServletResponse response, String message) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        HttpResponseMessageUtils.writeJson(response, RestfulApiRespFactory.badRequest(message));
    }

    private boolean ignoreSignCheck(HttpServletRequest request) {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            // 跳过预检查请求
            return true;
        }
        // 匹配任意一个则忽略签名检查
        return ignoreRequestMatchers.stream().anyMatch(requestMatcher -> requestMatcher.matches(request));
    }

    private SignatureRequest buildSignatureRequest(String accessKey, RequestSignMatcher matcher) throws IOException {
        HttpServletRequest request = matcher.request;
        SignatureRequest.SignatureRequestBuilder result = SignatureRequest.builder()
                // http 请求 path，不包含查询参数和域名
                .requestPath(request.getRequestURI())
                .queryString(request.getQueryString())
                .method(request.getMethod().toUpperCase())
                .nonce(request.getHeader(headerNames.nonce))
                .timestamp(request.getHeader(headerNames.timestamp))
                .secretKey(secretKeyProvider.apply(accessKey));
        if (matcher.signRequiredBody()) {
            result.requestBody(StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8));
        }
        return result.build();
    }

    @Override
    public int getOrder() {
        return WindWebFilterOrdered.REQUEST_SIGN_FILTER.getOrder();
    }

    private static class RequestSignMatcher {

        private final HttpServletRequest request;

        @Nullable
        private final MediaType contentType;

        public RequestSignMatcher(HttpServletRequest request) {
            this.contentType = StringUtils.hasLength(request.getContentType()) ? MediaType.parseMediaType(request.getContentType()) : null;
            if (signRequiredBody()) {
                // 创建一个可重复读的 request wrapper
                this.request = new RepeatableReadRequestWrapper(request);
            } else {
                // 签名不需要 body 参与
                this.request = request;
            }
        }

        boolean signRequiredBody() {
            return SIGNE_CONTENT_TYPES.stream().anyMatch(mediaType -> mediaType.includes(contentType));
        }

    }
}
