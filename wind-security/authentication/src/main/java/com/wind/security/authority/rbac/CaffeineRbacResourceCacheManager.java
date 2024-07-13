package com.wind.security.authority.rbac;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.wind.security.core.rbac.RbacResourceCacheManager;
import lombok.AllArgsConstructor;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * caffeine cache 适配器
 *
 * @author wuxp
 * @date 2023-10-22 11:02
 **/
@AllArgsConstructor
public class CaffeineRbacResourceCacheManager implements RbacResourceCacheManager {

    private final Map<String, Map<String, Object>> caches = new ConcurrentHashMap<>(8);

    private final Duration cacheEffectiveTime;

    @Override
    public Map<String, Object> load(String cacheName) {
        return caches.computeIfAbsent(cacheName, key -> buildRolesCaches(cacheEffectiveTime).asMap());
    }

    @Override
    public void store(String cacheName, Map<String, ?> cache) {
        Cache<String, Object> newCache = buildRolesCaches(cacheEffectiveTime);
        newCache.putAll(cache);
        caches.put(cacheName, newCache.asMap());
    }

    private static Cache<String, Object> buildRolesCaches(Duration cacheEffectiveTime) {
        return Caffeine.newBuilder()
                // 设置最后一次写入或访问后经过固定时间过期
                .expireAfterWrite(cacheEffectiveTime.getSeconds() + 10, TimeUnit.SECONDS)
                // 初始的缓存空间大小
                .initialCapacity(1000)
                // 缓存的最大条数
                .maximumSize(200000)
                .build();
    }

}
