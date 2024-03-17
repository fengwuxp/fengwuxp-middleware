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
     * 自动记录traceId
     */
    void trace();

    /**
     * 线程切换时记录traceId、和上下文变量
     *
     * @param traceId traceId
     */
    default void trace(@Null String traceId) {
        trace(traceId, Collections.emptyMap());
    }

    /**
     * 线程切换时记录traceId、和上下文变量
     *
     * @param traceId          traceId 如果为空则生成新的 traceId
     * @param contextVariables 复制记录的上下文变量
     */
    void trace(@Null String traceId, @NotNull Map<String, Object> contextVariables);

    /**
     * 清除线程上下文
     */
    void clearTraceContext();

    /**
     * 获取线程上下文中的 traceId
     *
     * @return trace id
     */
    default String getTraceId() {
        return getTraceContext().getTraceId();
    }

    /**
     * 获取线程上下文
     *
     * @return WindTraceContext
     */
    @NotNull
    WindTraceContext getTraceContext();
}


