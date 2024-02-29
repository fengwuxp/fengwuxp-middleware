package com.wind.server.web.security;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.wind.client.rest.ApiSignatureRequestInterceptor;
import com.wind.common.WindConstants;
import com.wind.common.WindHttpConstants;
import com.wind.common.exception.AssertUtils;
import com.wind.common.i18n.SpringI18nMessageUtils;
import com.wind.common.util.ServiceInfoUtils;
import com.wind.core.api.signature.ApiSecretAccount;
import com.wind.core.api.signature.ApiSignatureRequest;
import com.wind.core.api.signature.ApiSigner;
import com.wind.core.api.signature.SignatureHttpHeaderNames;
import com.wind.server.servlet.RepeatableReadRequestWrapper;
import com.wind.server.web.filters.WindWebFilterOrdered;
import com.wind.server.web.restful.RestfulApiRespFactory;
import com.wind.web.util.HttpResponseMessageUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;


/**
 * 接口请求验签
 * 参见：https://www.yuque.com/suiyuerufeng-akjad/wind/zl1ygpq3pitl00qp
 *
 * @author wuxp
 */
@Slf4j
@AllArgsConstructor
public class RequestSignFilter implements Filter, Ordered {

    private final Cache<String, ApiSecretAccount> accountCaches = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofHours(2))
            .maximumSize(1000)
            .build();

    private final SignatureHttpHeaderNames headerNames;

    private final Function<String, Collection<ApiSecretAccount>> apiSecretAccountProvider;

    /**
     * 忽略接口验签的请求匹配器
     */
    private final Collection<RequestMatcher> ignoreRequestMatchers;

    /**
     * 签名器
     */
    private final ApiSigner signer;

    /**
     * 是否启用
     */
    private final boolean enable;

    public RequestSignFilter(Function<String, Collection<ApiSecretAccount>> apiSecretAccountProvider, Collection<RequestMatcher> ignoreRequestMatchers, boolean enable) {
        this(WindConstants.WIND, apiSecretAccountProvider, ignoreRequestMatchers, enable);
    }

    public RequestSignFilter(String headerPrefix, Function<String, Collection<ApiSecretAccount>> apiSecretAccountProvider, Collection<RequestMatcher> ignoreRequestMatchers, boolean enable) {
        this(new SignatureHttpHeaderNames(headerPrefix), apiSecretAccountProvider, ignoreRequestMatchers, ApiSigner.HMAC_SHA256, enable);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        if (!enable || ignoreCheckSignature(request)) {
            log.debug("request no signature required, enable = {}", false);
            chain.doFilter(servletRequest, servletResponse);
            return;
        }
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String accessKey = request.getHeader(headerNames.getAccessKey());
        if (!StringUtils.hasLength(accessKey)) {
            badRequest(response, "request access key must not empty");
            return;
        }

        Collection<ApiSecretAccount> accounts = getApiSecretAccounts(accessKey);
        AssertUtils.notEmpty(accounts, "not found api secret account");
        MediaType contentType = StringUtils.hasLength(request.getContentType()) ? MediaType.parseMediaType(request.getContentType()) : null;
        boolean signRequiredBody = ApiSignatureRequestInterceptor.signRequiredRequestBody(contentType);
        HttpServletRequest httpRequest = signRequiredBody ? new RepeatableReadRequestWrapper(request) : request;
        ApiSignatureRequest signatureRequest = buildSignatureRequest(httpRequest, signRequiredBody);
        String requestSign = request.getHeader(headerNames.getSign());
        // 验签
        for (ApiSecretAccount account : accounts) {
            if (signer.verify(signatureRequest, account.getSecretKey(), requestSign)) {
                // 更新缓存
                accountCaches.put(accessKey, account);
                // 设置到签名认证账号到上下文中
                request.setAttribute(WindHttpConstants.API_SECRET_ACCOUNT_ATTRIBUTE_NAME, account);
                chain.doFilter(httpRequest, servletResponse);
                return;
            }
        }

        // 验证失败，移除缓存的账号
        accountCaches.invalidate(accessKey);
        if (!ServiceInfoUtils.isOnline()) {
            // 线下环境返回服务端的签名字符串，方便客户端排查签名错误
            response.addHeader(headerNames.getDebugSignContent(), signatureRequest.getSignTextForDigest());
            if (StringUtils.hasText(signatureRequest.getCanonicalizedQueryString())) {
                response.addHeader(headerNames.getDebugSignQuery(), signatureRequest.getCanonicalizedQueryString());
            }
        }
        badRequest(response, "sign verify error");
    }

    private Collection<ApiSecretAccount> getApiSecretAccounts(String accessKey) {
        ApiSecretAccount account = accountCaches.getIfPresent(accessKey);
        if (account == null) {
            // 缓存中不存在则加载秘钥账号，可能同时存在多个，用于替换秘钥的场景
            return apiSecretAccountProvider.apply(accessKey);
        }
        return Collections.singleton(account);
    }

    private void badRequest(HttpServletResponse response, String message) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        HttpResponseMessageUtils.writeJson(response, RestfulApiRespFactory.badRequest(SpringI18nMessageUtils.getMessage(message)));
    }

    private boolean ignoreCheckSignature(HttpServletRequest request) {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            // 跳过预检查请求
            return true;
        }
        // 匹配任意一个则忽略签名检查
        return ignoreRequestMatchers.stream().anyMatch(requestMatcher -> requestMatcher.matches(request));
    }

    private ApiSignatureRequest buildSignatureRequest(HttpServletRequest request, boolean requiredBody) throws IOException {
        ApiSignatureRequest.ApiSignatureRequestBuilder result = ApiSignatureRequest.builder()
                // http 请求 path，不包含查询参数和域名
                .requestPath(request.getRequestURI())
                .canonicalizedQueryString(request.getQueryString())
                // 仅在存在查询字符串时才设置，避免获取到表单参数
                .method(request.getMethod().toUpperCase())
                .nonce(request.getHeader(headerNames.getNonce()))
                .timestamp(request.getHeader(headerNames.getTimestamp()))
                // TODO 临时增加签名版本用于切换
                .version(request.getHeader("Signature-Version"));
        if (requiredBody) {
            result.requestBody(StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8));
        }
        return result.build();
    }

    @Override
    public int getOrder() {
        return WindWebFilterOrdered.REQUEST_SIGN_FILTER.getOrder();
    }
}
