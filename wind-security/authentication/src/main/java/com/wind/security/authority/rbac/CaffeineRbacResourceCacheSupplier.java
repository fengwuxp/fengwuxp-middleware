package com.wind.security.authority.rbac;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.wind.security.core.rbac.RbacResourceCache;
import com.wind.security.core.rbac.RbacResourceCacheSupplier;
import lombok.AllArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.lang.Nullable;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * caffeine cache 适配器
 *
 * @author wuxp
 * @date 2023-10-22 11:02
 **/
@AllArgsConstructor
public class CaffeineRbacResourceCacheSupplier implements RbacResourceCacheSupplier {

    private final Map<String, RbacResourceCache<String, Object>> caches = new ConcurrentHashMap<>(8);

    private final Duration cacheEffectiveTime;

    @Override
    public RbacResourceCache<String, Object> apply(String cacheName, RbacResourceCache.CacheLoader<String, Object> loader) {
        return caches.computeIfAbsent(cacheName, k -> buildCache(loader));
    }

    private RbacResourceCache<String, Object> buildCache(RbacResourceCache.CacheLoader<String, Object> loader) {
        return new CaffeineRbacResourceCache<>(cacheEffectiveTime, loader);
    }

    public static class CaffeineRbacResourceCache<K, V> implements RbacResourceCache<K, V> {

        private final Cache<K, V> cache;

        public CaffeineRbacResourceCache(Duration cacheEffectiveTime, RbacResourceCache.CacheLoader<K, V> loader) {
            this.cache = buildRolesCaches(cacheEffectiveTime, loader);
        }

        @Nullable
        @Override
        public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
            return cache.get(key, mappingFunction);
        }

        @Override
        public Map<K, V> getAllAsMap(Collection<K> keys, Function<Iterable<? extends @NonNull K>, @NonNull Map<K, V>> mappingFunction) {
            return cache.getAll(keys, mappingFunction);
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
        public Collection<K> keys() {
            return cache.asMap().keySet();
        }

        @Override
        public Collection<V> values() {
            return cache.asMap().values();
        }

        private static <K, V> Cache<K, V> buildRolesCaches(Duration cacheEffectiveTime, RbacResourceCache.CacheLoader<K, V> loader) {
            return Caffeine.newBuilder()
                    // 设置最后一次写入或访问后经过固定时间过期
                    .expireAfterWrite(cacheEffectiveTime.getSeconds() + 10, TimeUnit.SECONDS)
                    // 初始的缓存空间大小
                    .initialCapacity(1000)
                    // 缓存的最大条数
                    .maximumSize(200000)
                    .build(loader::load);
        }
    }
}
