package com.wind.server.web.filters;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.ImmutableSet;
import com.wind.common.utils.StringJoinSpiltUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Nonnull;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.UnaryOperator;

/**
 * 前后端分离模式下用于返回前端的 index.html 页面
 * 仅支持 browser 路由模式 https://juejin.cn/post/6844903648804208654
 *
 * @author wuxp
 * @date 2023-10-21 20:18
 **/
@AllArgsConstructor
public class IndexHtmlResourcesFilter extends OncePerRequestFilter {

    public static final String INDEX_HTML_NAME = "/index.html";

    /**
     * 请求 index.html 页面的请求路径
     */
    private static final Set<String> INDEX_HTML_PATHS = ImmutableSet.of("/", INDEX_HTML_NAME, "/index.htm", "/web");

    /**
     * 静态资源文件
     *
     * @key 路径
     * @value 内容
     */
    private static final Map<String, String> STATIC_RESOURCES = new ConcurrentHashMap<>();

    private static final PathMatcher PATH_MATCHER = new AntPathMatcher();

    /**
     * 忽略 swagger、webjars 下的静态资源
     */
    private static final Set<String> IGNORE_PATTERNS = ImmutableSet.of("/swagger-ui/**", "/swagger-resources/**", "/webjars/**");

    static {
        STATIC_RESOURCES.put(".js", "application/javascript");
        STATIC_RESOURCES.put(".css", "text/css");
        STATIC_RESOURCES.put(".svg", "image/svg");
        STATIC_RESOURCES.put(".webp", "image/webp");
        STATIC_RESOURCES.put(".gif", "image/webp");
        STATIC_RESOURCES.put(".png", "image/png");
        STATIC_RESOURCES.put(".jpg", "image/jpg");
        STATIC_RESOURCES.put(".jpeg", "image/jpeg");
        STATIC_RESOURCES.put(".ico", "image/ico");
        STATIC_RESOURCES.put(".ttf", "font/ttf");
        STATIC_RESOURCES.put(".otf", "font/otf");
        STATIC_RESOURCES.put(".woff", "font/woff");
        STATIC_RESOURCES.put(".ttc", "font/ttc");
    }

    /**
     * 前端路由前缀，仅支持 browser 模式下的路由
     */
    private final String routePrefix;

    /**
     * 资源加载器
     */
    private final UnaryOperator<String> resourceLoader;

    private final boolean enableCacheControl;

    /**
     * 资源缓存
     */
    private final Cache<String, String> resourcesCaches = Caffeine.newBuilder().expireAfterWrite(Duration.ofDays(1)).maximumSize(200).build();

    public IndexHtmlResourcesFilter(UnaryOperator<String> resourceLoader, boolean enableCacheControl) {
        this("/web/", resourceLoader, enableCacheControl);
    }

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull FilterChain chain) throws ServletException, IOException {
        if (Objects.equals(request.getMethod(), HttpMethod.GET.name())) {
            String requestUri = request.getRequestURI();
            if (IGNORE_PATTERNS.stream().anyMatch(pattern -> PATH_MATCHER.match(pattern, requestUri))) {
                // 忽略
                chain.doFilter(request, response);
                return;
            }
            boolean requestIndexHtml = matchesMediaType(request.getHeader(HttpHeaders.ACCEPT)) && (INDEX_HTML_PATHS.contains(requestUri) || requestUri.startsWith(routePrefix));
            if (requestIndexHtml) {
                // 写回 index.html
                response.setContentType(MediaType.TEXT_HTML_VALUE);
                response.getWriter().write(getResourceContent(INDEX_HTML_NAME));
                return;
            }
            Optional<String> optional = STATIC_RESOURCES.keySet().stream().filter(requestUri::endsWith).findFirst();
            if (optional.isPresent()) {
                // js css 资源访问
                response.setContentType(STATIC_RESOURCES.get(optional.get()));
                if (enableCacheControl) {
                    response.setHeader(HttpHeaders.CACHE_CONTROL, "max-age=63072000");
                }
                response.getWriter().write(getResourceContent(request.getRequestURI()));
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private String getResourceContent(String resourcePath) {
        return resourcesCaches.get(resourcePath, resourceLoader);
    }

    private boolean matchesMediaType(String mediaType) {
        return StringJoinSpiltUtils.spilt(mediaType).stream().map(MediaType::parseMediaType).anyMatch(media -> media.includes(MediaType.TEXT_HTML));
    }

}
