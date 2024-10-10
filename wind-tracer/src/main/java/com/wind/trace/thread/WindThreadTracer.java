package com.wind.trace.thread;


import com.wind.common.util.IpAddressUtils;
import com.wind.sequence.SequenceGenerator;
import com.wind.trace.WindTraceContext;
import com.wind.trace.WindTracer;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import static com.wind.common.WindConstants.LOCAL_HOST_IP_V4;
import static com.wind.common.WindConstants.TRACE_ID_NAME;

/**
 * 线程上下文 trace工具类
 *
 * @author wuxp
 * @date 2023-12-29 09:57
 **/
public final class WindThreadTracer implements WindTracer {

    static {
        // 默认填充本机 ip
        MDC.put(LOCAL_HOST_IP_V4, IpAddressUtils.getLocalIpv4WithCache());
    }

    /**
     * 生成 traceId
     */
    private static final SequenceGenerator TRACE_ID = () -> SequenceGenerator.randomAlphanumeric(32);

    @Override
    public void trace() {
        trace(TRACE_ID.next());
    }

    @Override
    public void trace(String traceId) {
        trace(traceId, Collections.emptyMap());
    }

    @Override
    public void trace(String traceId, @NotNull Map<String, Object> contextVariables) {
        //  使用 MDC 保存
        MDC.put(TRACE_ID_NAME, StringUtils.hasText(traceId) ? traceId : TRACE_ID.next());
        MDC.put(LOCAL_HOST_IP_V4, IpAddressUtils.getLocalIpv4WithCache());
        Objects.requireNonNull(contextVariables, "argument contextVariables must not null")
                .forEach((key, val) -> {
                    if (val instanceof String) {
                        MDC.put(key, (String) val);
                    }
                });

    }

    @Override
    public WindTraceContext getTraceContext() {
        Map<String, Object> mdcContext = getMdcContext();
        // TODO 待优化
        return new WindTraceContext() {
            @Override
            public String getTraceId() {
                String traceId = getContextVariable(TRACE_ID_NAME);
                if (traceId == null) {
                    // 没有则生成
                    traceId = TRACE_ID.next();
                    MDC.put(TRACE_ID_NAME, traceId);
                }
                return traceId;
            }

            @Override
            @NonNull
            public Map<String, Object> asContextVariables() {
                return mdcContext;
            }

            @Override
            @SuppressWarnings("unchecked")
            public <T> T getContextVariable(String variableName) {
                return (T) mdcContext.get(variableName);
            }
        };
    }

    @Override
    public void putContextVariable(String variableName, String variable) {
        MDC.put(variableName, variable);
    }

    @NonNull
    private static Map<String, Object> getMdcContext() {
        Map<String, String> context = MDC.getCopyOfContextMap();
        if (context == null) {
            context = Collections.singletonMap(TRACE_ID_NAME, TRACE_ID.next());
            context.forEach(MDC::put);
        }
        return Collections.unmodifiableMap(context);
    }

    @Override
    public void clear() {
        MDC.clear();
        // 保证 MDC 中一直存在 本机 ip 的属性 TODO 待优化
        MDC.put(LOCAL_HOST_IP_V4, IpAddressUtils.getLocalIpv4WithCache());
    }

}
