package com.wind.trace.thread;


import com.wind.common.util.IpAddressUtils;
import com.wind.sequence.SequenceGenerator;
import com.wind.trace.WindTraceContext;
import com.wind.trace.WindTracer;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import javax.validation.constraints.Null;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.wind.common.WindConstants.LOCAL_HOST_IP_V4;
import static com.wind.common.WindConstants.TRACE_ID_NAME;

/**
 * 线程上下文 trace工具类
 *
 * @author wuxp
 * @date 2023-12-29 09:57
 **/
public final class WindThreadTracer implements WindTracer {

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
    public void trace(String traceId, @Null Map<String, Object> contextVariables) {
        // TODO 暂时使用 MDC 保存
        MDC.put(TRACE_ID_NAME, StringUtils.hasText(traceId) ? traceId : TRACE_ID.next());
        contextVariables.forEach((k, val) -> {
            if (val instanceof String) {
                MDC.put(k, (String) val);
            }
        });
        MDC.put(LOCAL_HOST_IP_V4, IpAddressUtils.getLocalIpv4());
    }

    @Override
    public WindTraceContext getTraceContext() {
        Map<String, Object> mdc = getMdcContext();
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
            public Map<String, Object> asContextVariables() {
                return mdc;
            }

            @Override
            @SuppressWarnings("unchecked")
            public <T> T getContextVariable(String variableName) {
                return (T) mdc.get(variableName);
            }
        };
    }

    @Null
    private static Map<String, Object> getMdcContext() {
        Map<String, String> context = MDC.getCopyOfContextMap();
        if (context == null) {
            context = Collections.singletonMap(TRACE_ID_NAME, TRACE_ID.next());
            context.forEach(MDC::put);
        }
        return Collections.unmodifiableMap(new HashMap<>(context));
    }

    @Override
    public void clearTraceContext() {
        MDC.clear();
    }

}
