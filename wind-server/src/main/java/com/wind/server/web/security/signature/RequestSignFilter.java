package com.wind.server.web.security.signature;


import com.wind.client.rest.ApiSignatureRequestInterceptor;
import com.wind.client.util.HttpQueryUtils;
import com.wind.common.WindConstants;
import com.wind.common.WindHttpConstants;
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
import org.springframework.lang.Nullable;
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
import java.util.Collection;
import java.util.Objects;
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

    private final SignatureHttpHeaderNames headerNames;

    private final Function<String, ApiSecretAccount> apiSecretAccountProvider;

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

    public RequestSignFilter(Function<String, ApiSecretAccount> apiSecretAccountProvider, Collection<RequestMatcher> ignoreRequestMatchers, boolean enable) {
        this(WindConstants.WIND, apiSecretAccountProvider, ignoreRequestMatchers, enable);
    }

    public RequestSignFilter(String headerPrefix, Function<String, ApiSecretAccount> apiSecretAccountProvider, Collection<RequestMatcher> ignoreRequestMatchers, boolean enable) {
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
        RequestSignMatcher signMatcher = new RequestSignMatcher(request);
        ApiSignatureRequest apiSignatureRequest = buildSignatureRequest(accessKey, signMatcher);
        String requestSign = request.getHeader(headerNames.getSign());
        if (signer.verify(apiSignatureRequest, requestSign)) {
            chain.doFilter(signMatcher.request, servletResponse);
        } else {
            if (!ServiceInfoUtils.isOnline()) {
                // 线下环境返回服务端的签名，用于 debug
                response.addHeader(headerNames.getDebugSignContent(), apiSignatureRequest.getSignTextForDigest());
                if (apiSignatureRequest.getQueryString() != null || apiSignatureRequest.getQueryParams() != null) {
                    response.addHeader(headerNames.getDebugSignQuery(), apiSignatureRequest.getCanonicalizedQueryString());
                }
            }
            badRequest(response, "sign verify error");
        }
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

    private ApiSignatureRequest buildSignatureRequest(String accessKey, RequestSignMatcher matcher) throws IOException {
        HttpServletRequest request = matcher.request;
        // TODO 临时增加签名版本用于切换
        String version = request.getHeader("Signature-Version");
        ApiSecretAccount account = apiSecretAccountProvider.apply(accessKey);
        String queryString = request.getQueryString();
        ApiSignatureRequest.ApiSignatureRequestBuilder result = ApiSignatureRequest.builder()
                // http 请求 path，不包含查询参数和域名
                .requestPath(request.getRequestURI())
                // 如果是 v2 版本则使用 queryParams TODO 待删除
                .queryString(Objects.equals(version, "v2") ? null : fixQueryString(queryString))
                // 仅在存在查询字符串时才设置，避免获取到表单参数
                .queryParams(HttpQueryUtils.parseQueryParamsAsMap(queryString))
                .method(request.getMethod().toUpperCase())
                .nonce(request.getHeader(headerNames.getNonce()))
                .timestamp(request.getHeader(headerNames.getTimestamp()))
                .secretKey(account.getSecretKey());
        if (matcher.signRequiredBody()) {
            result.requestBody(StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8));
        }
        // 设置到签名认证账号到上下文中
        request.setAttribute(WindHttpConstants.API_SECRET_ACCOUNT_ATTRIBUTE_NAME, account);
        return result.build();
    }

    @Override
    public int getOrder() {
        return WindWebFilterOrdered.REQUEST_SIGN_FILTER.getOrder();
    }

    private static String fixQueryString(String queryString) {
        // @see https://juejin.cn/post/6844904034453864462#heading-2
        return queryString == null ? null : UriUtils.decode(queryString.replace("+", "%20"), StandardCharsets.UTF_8);
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
            return ApiSignatureRequestInterceptor.signUseRequestBody(contentType);
        }

    }
}
