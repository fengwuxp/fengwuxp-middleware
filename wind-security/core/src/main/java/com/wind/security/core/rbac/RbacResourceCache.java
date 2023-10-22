package com.wind.security.core.rbac;

import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.function.Function;

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
     * 添加值到缓存中
     *
     * @param key   缓存 key
     * @param value 缓存值
     */
    void put(K key, V value);

    /**
     * 从缓存中移除值
     *
     * @param key 缓存 key
     */
    void remove(K key);

    /**
     * 获取所有的缓存内容
     *
     * @return 缓存内容
     */
    Collection<V> values();
}
