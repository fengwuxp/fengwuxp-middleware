package com.wind.metrics;

/**
 * 指标查询器
 *
 * @author wuxp
 * @date 2024-09-10 13:51
 **/
public interface MetricsSearcher {

    <T> T getMetrics(String name, Class<?> clazz);
}
