package com.wind.common.config;

import com.alibaba.fastjson2.JSON;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Scheduler;
import com.wind.common.exception.AssertUtils;
import com.wind.common.spring.ApplicationContextUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.ConversionService;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author wuxp
 * @date 2023-11-15 09:47
 **/
public class WindSystemConfigRepository implements SystemConfigRepository {

    private static final String MARK_NULL = "_MARK_#_NULL_#_";

    private final SystemConfigStorage delegate;

    /**
     * 配置缓存
     */
    private final Cache<String, String> cache;

    public WindSystemConfigRepository(SystemConfigStorage delegate) {
        this(delegate, 90);
    }

    public WindSystemConfigRepository(SystemConfigStorage delegate, int cacheSeconds) {
        this.delegate = wrapper(delegate);
        this.cache = Caffeine.newBuilder()
                .refreshAfterWrite(cacheSeconds, TimeUnit.SECONDS)
                .maximumSize(500)
                .scheduler(Scheduler.forScheduledExecutorService(new ScheduledThreadPoolExecutor(1, new CustomizableThreadFactory("system-config-refresh"))))
                .build(this.delegate::getConfig);
    }

    @Override
    public void saveConfig(String name, String group, String value) {
        AssertUtils.hasText(name, "argument name must not empty");
        AssertUtils.hasText(group, "argument group must not empty");
        delegate.saveConfig(name, group, value);
    }

    @Nullable
    @Override
    public String getConfig(String name) {
        String result = cache.get(name, this.delegate::getConfig);
        return MARK_NULL.equals(result) ? null : result;
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

    private SystemConfigStorage wrapper(SystemConfigStorage storage) {
        return new SystemConfigStorage() {
            @Override
            public void saveConfig(String name, String group, String value) {
                storage.saveConfig(name, group, value);
            }

            @Nullable
            @Override
            public String getConfig(String name) {
                String result = storage.getConfig(name);
                // 避免返回 null ，缓存击穿
                return result == null ? MARK_NULL : result;
            }
        };
    }
}
