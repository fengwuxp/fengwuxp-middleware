package com.wind.security.core.rbac;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * rbac 缓存 适配器
 *
 * @author wuxp
 * @date 2023-10-22 11:14
 **/
public interface RbacResourceCache<K, V> {

    /**
     * 计算如果值不存在
     *
     * @param key             缓存 key
     * @param mappingFunction 值不存在时的加载函数
     * @return 只
     */
    @Nullable
    V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction);

    /**
     * 批量获取数据
     * 空数据将会被过滤
     *
     * @param keys key 列表
     * @return 结果集
     */
    default List<V> getAll(Collection<K> keys) {
        return getAllAsMap(keys).values().stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * 批量获取数据
     * 数据不存在的 keys 将会被忽略
     *
     * @param keys key 列表
     * @return Cache Map slice
     */
    default Map<K, V> getAllAsMap(Collection<K> keys) {
        return getAllAsMap(keys, ks -> Collections.emptyMap());
    }

    /**
     * 批量获取数据
     *
     * @param keys key 列表
     * @return Cache  Map slice
     */
    Map<K, V> getAllAsMap(Collection<K> keys, Function<Iterable<? extends @NonNull K>, @NonNull Map<K, V>> mappingFunction);

    /**
     * 添加值到缓存中
     *
     * @param key   缓存 key
     * @param value 缓存值
     */
    void put(K key, V value);

    /**
     * 添加值到缓存中
     *
     * @param cacheValues 缓存内容
     */
    void putAll(Map<K, V> cacheValues);

    /**
     * 从缓存中批量移除值
     *
     * @param keys 缓存 key
     */
    default void removeAll(Collection<K> keys) {
        keys.forEach(this::remove);
    }

    /**
     * 从缓存中移除值
     *
     * @param key 缓存 key
     */
    void remove(K key);

    /**
     * 获取所有的缓存 keys
     *
     * @return 缓存key
     */
    Collection<K> keys();

    /**
     * 获取所有的缓存内容
     *
     * @return 缓存内容
     */
    Collection<V> values();


    interface CacheLoader<K, V> {

        /**
         * Loads map value by key.
         *
         * @param key - map key
         * @return value or <code>null</code> if value doesn't exists
         */
        V load(K key);

        /**
         * Loads all keys.
         *
         * @return Iterable object. It's helpful if all keys don't fit in memory.
         */
        Iterable<K> loadAllKeys();

    }

}
