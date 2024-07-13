package com.wind.trace;

import com.wind.trace.thread.WindThreadTracer;

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
     * 获取 trace Context
     *
     * @return WindTraceContext
     */
    @NotNull
    WindTraceContext getTraceContext();
}


