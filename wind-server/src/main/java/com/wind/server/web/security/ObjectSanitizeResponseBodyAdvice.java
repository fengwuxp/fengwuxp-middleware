package com.wind.server.web.security;

import com.wind.common.query.supports.Pagination;
import com.wind.sensitive.DefaultObjectSanitizer;
import com.wind.server.web.supports.ApiResp;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Collection;
import java.util.Objects;

import static com.wind.common.WindConstants.ENABLED_NAME;
import static com.wind.common.WindConstants.TRUE;
import static com.wind.common.WindConstants.WIND_SERVER_OBJECT_SANITIZE_ADVICE;

/**
 * 脱敏处理
 *
 * @author wuxp
 * @date 2024-08-02 16:12
 **/
@Slf4j
@ConditionalOnProperty(prefix = WIND_SERVER_OBJECT_SANITIZE_ADVICE, value = ENABLED_NAME, havingValue = TRUE, matchIfMissing = true)
@RestControllerAdvice()
public class ObjectSanitizeResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private final DefaultObjectSanitizer sanitizer = new DefaultObjectSanitizer();

    @Override
    public boolean supports(MethodParameter returnType, @NotNull Class<? extends HttpMessageConverter<?>> converterType) {
        Class<?> returnTypeClass = Objects.requireNonNull(returnType.getMethod()).getReturnType();
        return returnTypeClass.isAssignableFrom(ApiResp.class) || sanitizer.requiredSanitize(returnTypeClass);
    }

    @Override
    public Object beforeBodyWrite(Object body, @NotNull MethodParameter returnType, @NotNull MediaType selectedContentType, @NotNull Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        // TODO 待优化
        if (body instanceof ApiResp) {
            sanitizeReturnValue(((ApiResp<?>) body).getData());
        } else {
            sanitizeReturnValue(body);
        }
        return body;
    }

    private void sanitizeReturnValue(Object result) {
        if (result instanceof Pagination) {
            // 分页对象
            ((Pagination<?>) result).getRecords().forEach(sanitizer::sanitize);
        } else if (result instanceof Collection) {
            // 集合对象
            ((Collection<?>) result).forEach(sanitizer::sanitize);

        } else {
            sanitizer.sanitize(result);
        }
    }
}
