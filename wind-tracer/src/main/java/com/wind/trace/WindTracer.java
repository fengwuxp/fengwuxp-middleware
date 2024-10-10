package com.wind.trace;

import com.wind.common.exception.AssertUtils;
import com.wind.trace.thread.WindThreadTracer;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Collections;
import java.util.Map;

/**
 * 请求 or 线程 tracer
 *
 * @author wuxp
 * @date 2023-12-29 10:13
 **/
public interface WindTracer {

    /**
     * 默认的 tracer
     */
    WindTracer TRACER = new WindThreadTracer();

    /**
     * 自动生成 traceId 并设置到上下文中
     *
     * @see #trace(String)
     */
    void trace();

    /**
     * 通过传入的 traceId 设置到上下文中，如果 traceId 为空，则生成新的 traceId
     *
     * @param traceId traceId 如果为空则生成新的 traceId
     */
    default void trace(@Null String traceId) {
        trace(traceId, Collections.emptyMap());
    }

    /**
     * 通过传入的 traceId、 contextVariables 设置到上下文中，如果 traceId 为空，则生成新的 traceId
     *
     * @param traceId          traceId 如果为空则生成新的 traceId
     * @param contextVariables trace 上下文变量
     */
    void trace(@Null String traceId, @NotNull Map<String, Object> contextVariables);

    /**
     * 清除 trace 上下文
     */
    void clear();

    /**
     * 获取线程上下文中的 traceId
     *
     * @return trace id
     */
    default String getTraceId() {
        return getTraceContext().getTraceId();
    }

    /**
     * 添加上下文变量
     *
     * @param variableName 变量名称
     * @param variable     变量值
     */
    void putContextVariable(@NotBlank String variableName, @NotNull String variable);

    /**
     * 添加上下文变量
     *
     * @param variables 变量 map
     */
    default void putContextVariables(@NotNull Map<String, String> variables) {
        AssertUtils.notNull(variables, "argument variables must not null");
        variables.forEach(this::putContextVariable);
    }

    /**
     * 获取 trace Context
     *
     * @return WindTraceContext
     */
    @NotNull
    WindTraceContext getTraceContext();
}


