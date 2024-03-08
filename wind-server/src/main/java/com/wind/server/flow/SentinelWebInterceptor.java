package com.wind.server.flow;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.wind.common.exception.DefaultExceptionCode;
import com.wind.common.i18n.SpringI18nMessageUtils;
import com.wind.sentinel.FlowResource;
import com.wind.server.web.restful.RestfulApiRespFactory;
import com.wind.web.util.HttpResponseMessageUtils;
import com.wind.web.util.HttpServletRequestUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.function.Function;

/**
 * sentinel web interceptor
 *
 * @author wuxp
 * @date 2024-03-07 17:28
 **/
@Slf4j
@AllArgsConstructor
public class SentinelWebInterceptor implements HandlerInterceptor {

    private static final String SENTINEL_ENTRY_ATTRIBUTE_NAME = SentinelWebInterceptor.class.getName() + ".entry";

    private final Function<HttpServletRequest, FlowResource> resourceProvider;

    private final BlockExceptionHandler blockExceptionHandler;

    public SentinelWebInterceptor(Function<HttpServletRequest, FlowResource> resourceProvider) {
        this(resourceProvider, SentinelWebInterceptor::defaultHandleBlockException);
    }

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        FlowResource resource = resourceProvider.apply(request);
        if (resource == null || ObjectUtils.isEmpty(resource.getName())) {
            log.debug("no resource found in request, request uri = {}", request.getRequestURI());
            return true;
        }
        ContextUtil.enter(resource.getContextName(), resource.getOrigin());
        try {
            Entry entry = SphU.entry(resource.getName(), resource.getResourceType(), resource.getEntryType());
            request.setAttribute(SENTINEL_ENTRY_ATTRIBUTE_NAME, entry);
        } catch (BlockException exception) {
            try {
                blockExceptionHandler.handle(request, response, exception);
            } finally {
                ContextUtil.exit();
            }
            return false;
        }
        return true;
    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, Exception exception) {
        Entry entry = HttpServletRequestUtils.getRequestAttribute(SENTINEL_ENTRY_ATTRIBUTE_NAME, request);
        if (entry == null) {
            // should not happen
            log.warn("no entry found in request, key = {}", SENTINEL_ENTRY_ATTRIBUTE_NAME);
            return;
        }
        request.removeAttribute(SENTINEL_ENTRY_ATTRIBUTE_NAME);
        if (exception != null) {
            Tracer.traceEntry(exception, entry);
        }
        ContextUtil.exit();
    }

    private static void defaultHandleBlockException(HttpServletRequest request, HttpServletResponse response, BlockException exception) {
        String message = SpringI18nMessageUtils.getMessage(DefaultExceptionCode.TO_MANY_REQUESTS.getDesc());
        HttpResponseMessageUtils.writeApiResp(response, RestfulApiRespFactory.toManyRequests(message));
    }
}
