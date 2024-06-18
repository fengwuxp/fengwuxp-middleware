package com.wind.sentinel;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

/**
 * @author wuxp
 * @date 2024-06-14 18:04
 **/
public class MetricsTests {

    public static void main(String[] args) {
        // 创建一个简单的MeterRegistry，用于演示
        MeterRegistry registry = new SimpleMeterRegistry();

        // 添加一个名为"my.counter"的计数器，初始值为0
        registry.counter("my.counter");

        // 增加计数器的值
        registry.counter("my.counter", "tag", "value").increment(23);

        // 获取计数器的当前值
        double counterValue = registry.counter("my.counter", "tag", "value").count();
        System.out.println(counterValue);

    }
}
