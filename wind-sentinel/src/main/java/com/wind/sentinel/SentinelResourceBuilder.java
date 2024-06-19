package com.wind.sentinel;

import com.alibaba.csp.sentinel.EntryType;
import com.wind.common.WindConstants;
import com.wind.common.exception.AssertUtils;
import com.wind.common.util.ServiceInfoUtils;
import io.micrometer.core.instrument.Tag;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author wuxp
 * @date 2024-06-19 15:15
 **/
public final class SentinelResourceBuilder {

    private SentinelResourceBuilder() {
        throw new AssertionError();
    }

    /**
     * rocket mq resource 资源构建者
     *
     * @return 资源构建者
     */
    public static RocketMqConsumerResourceBuilder rocketConsumer() {
        return new RocketMqConsumerResourceBuilder();
    }


    public final static class RocketMqConsumerResourceBuilder {

        private String groupName;

        private String topic;

        private String tag;

        private Iterable<Tag> metricsTags = Collections.emptyList();

        private RocketMqConsumerResourceBuilder() {
        }

        public RocketMqConsumerResourceBuilder groupName(String groupName) {
            AssertUtils.hasText(groupName, "groupName must not empty");
            this.groupName = groupName;
            return this;
        }

        public RocketMqConsumerResourceBuilder topic(String topic) {
            AssertUtils.hasText(topic, "topic must not empty");
            this.topic = topic;
            return this;
        }

        public RocketMqConsumerResourceBuilder tag(String tag) {
            this.tag = tag;
            return this;
        }

        public RocketMqConsumerResourceBuilder metricsTags(Iterable<Tag> metricsTags) {
            AssertUtils.notNull(metricsTags, "metricsTags must not null");
            this.metricsTags = metricsTags;
            return this;
        }

        public SentinelResource build() {
            DefaultSentinelResource result = new DefaultSentinelResource();
            String name = Stream.of(groupName, topic, tag).filter(StringUtils::hasText).collect(Collectors.joining(WindConstants.COLON));
            result.setName(String.format("%s@%s", SentinelResourcesType.ROCKETMQ_CONSUMER.getTypeName(), name));
            result.setResourceType(SentinelResourcesType.ROCKETMQ_CONSUMER.getCode());
            result.setEntryType(EntryType.OUT);
            result.setOrigin(topic);
            result.setContextName(ServiceInfoUtils.getApplicationName());
            result.setMetricsTags(metricsTags);
            return result;
        }
    }
}
