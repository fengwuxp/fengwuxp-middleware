package com.wind.trace.thread;

import com.wind.common.exception.AssertUtils;
import com.wind.trace.WindTraceContext;
import com.wind.trace.WindTracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskDecorator;
import org.springframework.lang.NonNull;

/**
 * 用于在线程切换时，将 trace 上下文信息复制到新的线程中
 *
 * @author wuxp
 * @date 2023-12-29 09:55
 **/
@Slf4j
public abstract class TraceContextTask implements TaskDecorator {

    @Override
    @NonNull
    public Runnable decorate(@NonNull Runnable task) {
        AssertUtils.notNull(task, "argument task must not null");
        // 获取当前线程的上下文
        WindTraceContext context = WindTracer.TRACER.getTraceContext();
        return () -> {
            try {
                if (log.isDebugEnabled()) {
                    log.debug("task decorate, trace context: {}", context);
                }
                traceContext();
                // 线程切换，复制上下文和 traceId
                WindTracer.TRACER.trace(context.getTraceId(), context.asContextVariables());
                task.run();
            } finally {
                if (log.isDebugEnabled()) {
                    log.debug("task decorate, clear trace context");
                }
                // 清除线程上下文
                WindTracer.TRACER.clear();
                clearContext();
            }
        };
    }

    /**
     * 自定义的 trace
     */
    protected void traceContext() {
        // 子类自行实现
    }

    /**
     * 自定义的 clear context
     */
    protected void clearContext() {
        // 子类自行实现
    }

    public static TaskDecorator of() {
        return new TraceContextTask() {
        };
    }
}
