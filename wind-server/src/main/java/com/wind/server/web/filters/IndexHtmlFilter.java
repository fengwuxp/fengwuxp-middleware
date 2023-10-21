package com.wind.server.web.filters;

import com.google.common.collect.ImmutableSet;
import com.wind.common.exception.AssertUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Nonnull;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Set;

/**
 * 前后端分离模式下用于返回前端的 index.html 页面
 * 仅支持 browser 路由模式 https://juejin.cn/post/6844903648804208654
 *
 * @author wuxp
 * @date 2023-10-21 20:18
 **/
@AllArgsConstructor
public class IndexHtmlFilter extends OncePerRequestFilter {

    private static final RestTemplate REST_TEMPLATE = new RestTemplate();

    private static final Set<String> INDEX_HTML_PATHS = ImmutableSet.of("/", "/index.html", "/index.htm");

    /**
     * 前端路由前缀，仅支持 browser 模式下的路由
     */
    private final String routePrefix;

    /**
     * html content
     */
    private final byte[] bytes;

    /**
     * @param routePrefix 路由前缀
     * @param indexUrl    index.html 文件资源所在的远程地址
     */
    public IndexHtmlFilter(String routePrefix, String indexUrl) {
        this(routePrefix, getIndexHtmlContent(indexUrl));
    }

    public IndexHtmlFilter(String indexUrl) {
        this("/web/", indexUrl);
    }

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull FilterChain chain) throws ServletException, IOException {
        if (isAccessIndexView(request)) {
            // 写回前端前端页面
            response.setContentLength(bytes.length);
            response.setContentType(MediaType.TEXT_HTML_VALUE);
            response.getWriter().write(new String(bytes, StandardCharsets.UTF_8));
            return;
        }
        chain.doFilter(request, response);
    }

    private boolean isAccessIndexView(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        return Objects.equals(request.getMethod(), HttpMethod.GET.name())
                && MediaType.parseMediaType(request.getHeader(HttpHeaders.ACCEPT)).includes(MediaType.TEXT_HTML)
                && INDEX_HTML_PATHS.contains(requestUri) || requestUri.startsWith(routePrefix);
    }

    private static byte[] getIndexHtmlContent(String url) {
        String result = REST_TEMPLATE.getForObject(url, String.class);
        AssertUtils.notNull(result, String.format("get index.html content error，url =%s", url));
        return result.getBytes(StandardCharsets.UTF_8);
    }
}
