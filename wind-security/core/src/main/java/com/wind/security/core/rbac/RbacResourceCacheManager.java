package com.wind.security.core.rbac;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * rbac资源缓存管理
 *
 * @author wuxp
 * @date 2023-10-23 09:47
 **/
public interface RbacResourceCacheManager {

    /**
     * 加载缓存
     *
     * @param cacheName 缓存名称
     * @return 缓存
     */
    @NotNull
    Map<String, Object> load(@NotBlank String cacheName);

    /**
     * 存储缓存
     *
     * @param cacheName 缓存名称
     * @param cache     缓存
     */
    void store(@NotBlank String cacheName, @NotNull Map<String, ?> cache);
}
