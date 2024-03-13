package com.wind.sentinel.metrics;


import com.alibaba.csp.sentinel.config.SentinelConfig;
import com.alibaba.csp.sentinel.metric.extension.MetricExtension;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Sentinel Metrics 采集
 *
 * @author wuxp
 * @date 2024-03-13 11:16
 * @see com.alibaba.csp.sentinel.metric.extension.MetricExtensionProvider
 */
public class SentinelMetricsCollector implements MetricExtension {

    /**
     * Prefix used for all Sentinel metric names.
     */
    private static final String SENTINEL_METRIC_NAME_PREFIX = "sentinel." + SentinelConfig.getAppName();

    /**
     * 请求通过 Metric name
     */
    private static final String PASS_REQUESTS_TOTAL = SENTINEL_METRIC_NAME_PREFIX + ".pass.requests.total";
    /**
     * 触发流控
     */
    private static final String BLOCK_REQUESTS_TOTAL = SENTINEL_METRIC_NAME_PREFIX + ".block.requests.total";

    /**
     * 请求成功
     */
    private static final String SUCCESS_REQUESTS_TOTAL = SENTINEL_METRIC_NAME_PREFIX + ".success.requests.total";

    /**
     * 请求异常
     */
    private static final String EXCEPTION_REQUESTS_TOTAL = SENTINEL_METRIC_NAME_PREFIX + ".exception_requests_total";

    /**
     * 延迟分布
     */
    private static final String REQUESTS_LATENCY_SECONDS = SENTINEL_METRIC_NAME_PREFIX + ".requests.latency.seconds";

    /**
     * 资源当前线程数
     */
    private static final String CURRENT_THREADS = SENTINEL_METRIC_NAME_PREFIX + ".current.threads";

    private static final String RESOURCE_TAG_NAME = "resource";

    private static final String BLOCK_EXCEPTION_TAG_NAME = "blockExceptionType";

    private static final String EXCEPTION_TAG_NAME = "exceptionType";

    private static final String APP_TAG_NAME = "appName";

    private static final String ORIGIN_TAG_NAME = "origin";

    private static final Map<String, AtomicLong> RESOURCE_THREAD_COUNTERS = new ConcurrentHashMap<>();

    @Override
    public void addPass(String resource, int n, Object... args) {
        Metrics.counter(PASS_REQUESTS_TOTAL, getResourceTags(resource, args)).increment(n);
    }

    @Override
    public void addBlock(String resource, int n, String origin, BlockException ex, Object... args) {
        List<Tag> tags = new ArrayList<>(getArgsTags(args));
        tags.addAll(Arrays.asList(
                Tag.of(RESOURCE_TAG_NAME, ex.getRule().getResource()),
                Tag.of(BLOCK_EXCEPTION_TAG_NAME, ex.getClass().getSimpleName()),
                Tag.of(APP_TAG_NAME, ex.getRuleLimitApp()),
                Tag.of(ORIGIN_TAG_NAME, origin)
        ));
        Metrics.counter(BLOCK_REQUESTS_TOTAL, tags).increment(n);
    }

    @Override
    public void addSuccess(String resource, int n, Object... args) {
        Metrics.counter(SUCCESS_REQUESTS_TOTAL, getResourceTags(resource, args)).increment(n);
    }

    @Override
    public void addException(String resource, int n, Throwable throwable) {
        Tags tags = Tags.of(RESOURCE_TAG_NAME, resource, EXCEPTION_TAG_NAME, throwable.getClass().getSimpleName());
        Metrics.counter(EXCEPTION_REQUESTS_TOTAL, tags).increment(n);
    }

    @Override
    public void addRt(String resource, long rt, Object... args) {
        Metrics.timer(REQUESTS_LATENCY_SECONDS, getResourceTags(resource, args)).record(rt, TimeUnit.MICROSECONDS);
    }

    @Override
    public void increaseThreadNum(String resource, Object... args) {
        Metrics.gauge(CURRENT_THREADS, getResourceTags(resource, args), getThreadCounter(resource), AtomicLong::incrementAndGet);
    }

    @Override
    public void decreaseThreadNum(String resource, Object... args) {
        Metrics.gauge(CURRENT_THREADS, getResourceTags(resource, args), getThreadCounter(resource), AtomicLong::decrementAndGet);
    }


    private AtomicLong getThreadCounter(String resource) {
        return RESOURCE_THREAD_COUNTERS.computeIfAbsent(resource, k -> new AtomicLong(0));
    }

    private List<Tag> getResourceTags(String resource, Object[] args) {
        List<Tag> result = new ArrayList<>(getArgsTags(args));
        result.add(Tag.of(RESOURCE_TAG_NAME, resource));
        return result;
    }

    private List<Tag> getArgsTags(Object[] args) {
        return Arrays.stream(args)
                .filter(Objects::nonNull)
                .filter(Tags.class::isInstance)
                .map(Tags.class::cast)
                .flatMap(Tags::stream)
                .collect(Collectors.toList());
    }
}