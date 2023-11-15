package com.wind.common.config;

import com.alibaba.fastjson2.JSON;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Scheduler;
import com.wind.common.spring.ApplicationContextUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.ConversionService;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * @author wuxp
 * @date 2023-11-15 09:47
 **/

public class WindSystemConfigRepository implements SystemConfigRepository {

    private final UnaryOperator<String> supplier;

    /**
     * 配置缓存
     */
    private final Cache<String, String> cache;

    public WindSystemConfigRepository(UnaryOperator<String> supplier) {
        this(supplier, 90);
    }

    public WindSystemConfigRepository(UnaryOperator<String> supplier, int cacheSeconds) {
        this.supplier = supplier;
        this.cache = Caffeine.newBuilder()
                .refreshAfterWrite(cacheSeconds, TimeUnit.SECONDS)
                .maximumSize(500)
                .scheduler(Scheduler.forScheduledExecutorService(new ScheduledThreadPoolExecutor(1, new CustomizableThreadFactory("system-config-refresh"))))
                .build(supplier::apply);
    }

    @Nullable
    @Override
    public String getConfig(String name) {
        return cache.get(name, this.supplier);
    }

    @Nullable
    @Override
    public <T> T getConfig(String name, Class<T> targetType, T defaultValue) {
        String result = getConfig(name);
        if (result == null) {
            return defaultValue;
        }
        return convert(result, targetType);
    }

    @Override
    public <T> Set<T> getConfigs(String name, Class<T> clazz) {
        Set<String> configs = getConfigs(name);
        return configs.stream().map(text -> convert(text, clazz)).collect(Collectors.toSet());
    }

    @Nullable
    private <T> T convert(String val, Class<T> targetType) {
        ConversionService service = ApplicationContextUtils.getBean(ConversionService.class);
        return service.convert(val, targetType);
    }

    @Nullable
    @Override
    public <T> T getJsonConfig(String name, Class<T> targetType) {
        String json = getConfig(name);
        if (json == null) {
            return null;
        }
        return JSON.parseObject(json, targetType);
    }

    @Nullable
    @Override
    public <T> T getJsonConfig(String name, ParameterizedTypeReference<T> targetType) {
        String json = getConfig(name);
        if (json == null) {
            return null;
        }
        return JSON.parseObject(json, targetType.getType());
    }
}
