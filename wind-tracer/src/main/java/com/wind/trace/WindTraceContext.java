package com.wind.trace;

import org.springframework.lang.Nullable;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;

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
    @NotBlank
    String getTraceId();

    /**
     * trace 上下文变量
     *
     * @return 不可变的 Map 对象
     */
    Map<String, Object> asContextVariables();

    /**
     * 获取上下文变量
     *
     * @param variableName 变量名称
     * @return 上下文变量值
     */
    @Nullable
    <T> T getContextVariable(String variableName);
}
