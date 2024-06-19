package com.wind.sentinel;

import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author wuxp
 * @date 2024-06-19 11:09
 **/
public final class SentinelFlowTestUtils {

    private SentinelFlowTestUtils() {
        throw new AssertionError();
    }

    public static List<FlowRule> mockFlowRules(String... names) {
        AtomicInteger counter = new AtomicInteger(0);
        return Arrays.stream(names)
                .map(n -> mockFlowRule(String.format("%s%s", n, counter.incrementAndGet() + "")))
                .collect(Collectors.toList());
    }

    public static FlowRule mockFlowRule(String name) {
        FlowRule result = new FlowRule();
        result.setResource(name);
        result.setCount(1);
        return result;
    }
}
