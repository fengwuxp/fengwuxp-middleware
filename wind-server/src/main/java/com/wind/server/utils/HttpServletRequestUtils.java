package com.wind.server.utils;

import com.wind.common.exception.AssertUtils;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 从上下文中获取 http servlet request
 *
 * @author wuxp
 * @date 2023-09-28 07:32
 **/
public final class HttpServletRequestUtils {

    private HttpServletRequestUtils() {
        throw new AssertionError();
    }

    public static HttpServletRequest requiredContextRequest() {
        HttpServletRequest result = getContextRequestOfNullable();
        AssertUtils.notNull(result, "not currently in web servlet context");
        return result;
    }

    @Nullable
    public static HttpServletRequest getContextRequestOfNullable() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return null;
        }
        return ((ServletRequestAttributes) requestAttributes).getRequest();
    }
}
