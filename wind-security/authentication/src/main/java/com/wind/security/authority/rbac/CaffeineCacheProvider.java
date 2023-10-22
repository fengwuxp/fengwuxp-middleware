package com.wind.security.authority.rbac;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.wind.security.core.rbac.RbacResourceCache;
import lombok.AllArgsConstructor;
import org.springframework.lang.Nullable;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * caffeine cache 适配器
 *
 * @author wuxp
 * @date 2023-10-22 11:02
 **/
@AllArgsConstructor
public class CaffeineCacheProvider implements BiFunction<String, Executor, RbacResourceCache<String, Object>> {

    private final Map<String, RbacResourceCache<String, Object>> caches = new ConcurrentHashMap<>();

    private final Duration cacheEffectiveTime;

    @Override
    public RbacResourceCache<String, Object> apply(String cacheName, Executor executor) {
        return caches.computeIfAbsent(cacheName, k -> buildCache(executor));
    }

    private RbacResourceCache<String, Object> buildCache(Executor executor) {
        return new CaffeineRbacResourceCache<>(buildRolesCaches(executor));
    }

    private Cache<String, Object> buildRolesCaches(Executor executor) {
        return Caffeine.newBuilder()
                .executor(executor)
                // 设置最后一次写入或访问后经过固定时间过期
                .expireAfterWrite(cacheEffectiveTime.getSeconds() + 10, TimeUnit.SECONDS)
                // 初始的缓存空间大小
                .initialCapacity(200)
                // 缓存的最大条数
                .maximumSize(2000).build();
    }

    @AllArgsConstructor
    static class CaffeineRbacResourceCache<K, V> implements RbacResourceCache<K, V> {

        private final Cache<K, V> cache;

        @Nullable
        @Override
        public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
            return cache.get(key, mappingFunction);
        }

        @Override
        public void put(K key, V value) {
            cache.put(key, value);
        }

        @Override
        public void remove(K key) {
            cache.invalidate(key);
        }

        @Override
        public Collection<V> values() {
            return cache.asMap().values();
        }
    }
}
