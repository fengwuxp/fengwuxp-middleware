package com.wind.rocketmq.sentinel;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.metric.extension.MetricExtensionProvider;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.fastjson2.JSON;
import com.wind.common.WindConstants;
import com.wind.common.util.ServiceInfoUtils;
import com.wind.sentinel.DefaultSentinelResource;
import com.wind.sentinel.SentinelResource;
import com.wind.sentinel.SentinelResourcesType;
import com.wind.sentinel.metrics.SentinelMetricsCollector;
import io.micrometer.core.instrument.Tags;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Date;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 消息监听流量限制器
 *
 * @author wuxp
 * @date 2024-06-18 10:12
 **/
@Slf4j

public final class SentinelFlowMessageListenLimiter {

    private static final String DEFAULT_SENTINEL_ENTRY_ATTRIBUTE_NAME = SentinelFlowMessageListenLimiter.class.getName() + ".entry";

    static {
        // 增加自定义的指标收集器
        MetricExtensionProvider.addMetricExtension(new SentinelMetricsCollector(SentinelResourcesType.ROCKETMQ_CONSUMER.getTypeName()));
    }

    private SentinelFlowMessageListenLimiter() {
        throw new AssertionError();
    }

    /**
     * 消息消费流控
     *
     * @param groupName groupName
     * @param message   消息体
     * @return 完成流控的 {@link Consumer} 实例
     * @throws BlockException 流控异常
     */
    public static Consumer<Exception> flowControl(String groupName, MessageExt message) throws BlockException {
        SentinelResource resource = buildResource(groupName, message.getTopic(), message.getTags());
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

    private static SentinelResource buildResource(String groupName, String topic, String tag) {
        DefaultSentinelResource result = new DefaultSentinelResource();
        String name = Stream.of(groupName, topic, tag).filter(StringUtils::hasText).collect(Collectors.joining(WindConstants.COLON));
        result.setName(String.format("%s@%s", SentinelResourcesType.ROCKETMQ_CONSUMER.getTypeName(), name));
        result.setResourceType(SentinelResourcesType.ROCKETMQ_CONSUMER.getCode());
        result.setEntryType(EntryType.OUT);
        result.setOrigin(topic);
        result.setContextName(ServiceInfoUtils.getApplicationName());
        return result;
    }
}
