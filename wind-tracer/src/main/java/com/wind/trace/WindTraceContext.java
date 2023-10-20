package com.wind.trace;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Map;

import static com.wind.common.WindHttpConstants.HTTP_REQUEST_IP_ATTRIBUTE_NAME;
import static com.wind.common.WindHttpConstants.HTTP_USER_AGENT_HEADER_NAME;

/**
 * trace context
 *
 * @author wuxp
 * @date 2023-10-20 10:31
 **/
public interface WindTraceContext {

    /**
     * 获取 trace id
     *
     * @return traceId
     */
    @NonNull
    String getTraceId();

    /**
     * trace 上下文变量
     *
     * @return 不可变的 Map 对象
     */
    Map<String, String> asContextVariables();

    /**
     * 获取上下文变量
     *
     * @param variableName 变量名称
     * @return 上下文变量值
     */
    @Nullable
    String getContextVariable(String variableName);

    /**
     * 获取访问来源 ip
     *
     * @return ip
     */
    @Nullable
    default String getRequestSourceIp() {
        return getContextVariable(HTTP_REQUEST_IP_ATTRIBUTE_NAME);
    }

    /**
     * 获取访问的用户代理
     *
     * @return User-Agent
     */
    @Nullable
    default String getUserAgent() {
        return getContextVariable(HTTP_USER_AGENT_HEADER_NAME);
    }
}
