package com.wind.sentinel.util;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.wind.sentinel.SentinelResource;
import io.micrometer.core.instrument.Tags;

import java.util.function.Consumer;

/**
 * sentinel 限流工具类
 *
 * @author wuxp
 * @date 2024-06-19 10:24
 **/
public final class SentinelFlowLimitUtils {

    /**
     * 资源流控
     *
     * @param resource 限流资源
     * @return 完成流控的 {@link Consumer} 实例
     * @throws BlockException 流控异常
     */
    public static Consumer<Exception> flowControl(SentinelResource resource) throws BlockException {
        ContextUtil.enter(resource.getContextName(), resource.getOrigin());
        final Entry entry = SphU.entry(resource.getName(), resource.getResourceType(), resource.getEntryType(), new Object[]{Tags.of(resource.getMetricsTags())});
        return exception -> {
            entry.exit();
            if (exception != null) {
                Tracer.traceEntry(exception, entry);
            }
            ContextUtil.exit();
        };
    }
}
