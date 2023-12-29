package com.wind.web.utils;

import com.wind.trace.WindTracer;

import javax.annotation.Nullable;

import static com.wind.common.WindHttpConstants.HTTP_REQUEST_IP_ATTRIBUTE_NAME;

/**
 * http trace variable 变量获取
 *
 * @author wuxp
 * @date 2023-12-29 10:58
 **/
public final class HttpTraceVariableUtils {

    private HttpTraceVariableUtils() {
        throw new AssertionError();
    }

    @Nullable
    public static String getRequestSourceIp() {
        return WindTracer.TRACER.getTraceContext().getContextVariable(HTTP_REQUEST_IP_ATTRIBUTE_NAME);
    }
}
