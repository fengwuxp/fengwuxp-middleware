package com.wind.sentinel;

import com.alibaba.csp.sentinel.concurrent.NamedThreadFactory;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 定时刷新规则的监听器
 *
 * @author wuxp
 * @date 2024-03-07 16:20
 **/
@Slf4j
public class TimerRefreshFlowRuleListener implements FlowRuleListener {

    private final Supplier<List<FlowRule>> supplier;

    private final ScheduledThreadPoolExecutor scheduler;

    private final Duration refreshInterval;

    public TimerRefreshFlowRuleListener(Supplier<List<FlowRule>> supplier, Duration refreshInterval) {
        this.supplier = supplier;
        this.refreshInterval = refreshInterval;
        this.scheduler = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("sentinel-flow-rule-refresh"));
        refresh();
    }

    public TimerRefreshFlowRuleListener(Supplier<List<FlowRule>> supplier) {
        this(supplier, Duration.ofMinutes(5));
    }

    @Override
    public void onChange(List<FlowRule> rules) {
        if (rules == null) {
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("refresh sentinel flow roles, {}", rules);
        }
        FlowRuleManager.loadRules(rules);
    }

    private void refresh() {
        onChange(supplier.get());
        nextRefresh();
    }

    private void nextRefresh() {
        scheduler.schedule(this::refresh, refreshInterval.getSeconds(), TimeUnit.SECONDS);
    }
}
