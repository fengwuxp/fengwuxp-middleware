package com.wind.security.authority.rbac;

import com.google.common.collect.ImmutableSet;
import com.wind.common.exception.AssertUtils;
import com.wind.common.locks.LockFactory;
import com.wind.security.core.rbac.RbacResource;
import com.wind.security.core.rbac.RbacResourceCacheManager;
import com.wind.security.core.rbac.RbacResourceChangeEvent;
import com.wind.security.core.rbac.RbacResourceService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.wind.security.WebSecurityConstants.RBAC_PERMISSION_CACHE_NAME;
import static com.wind.security.WebSecurityConstants.RBAC_ROLE_CACHE_NAME;
import static com.wind.security.WebSecurityConstants.RBAC_USER_ROLE_CACHE_NAME;

/**
 * 基于缓存刷新的rbac资源服务
 *
 * @author wuxp
 * @date 2024-05-15 18:18
 **/
@AllArgsConstructor
@Slf4j
public class CacheRbacResourceService implements RbacResourceService, ApplicationListener<RbacResourceChangeEvent>, DisposableBean {

    private static final String REFRESH_RBAC_CACHE_LOCK_KEY = "REFRESH_RBAC_CACHE_LOCK";

    private final RbacResourceService delegate;

    private final RbacResourceCacheManager cacheManager;

    private final LockFactory lockFactory;

    /**
     * 缓存刷新间隔
     */
    private final Duration refreshInterval;

    private final ScheduledExecutorService schedule = new ScheduledThreadPoolExecutor(1, new CustomizableThreadFactory("web-rbac-resource-refresher"));

    public CacheRbacResourceService(RbacResourceService delegate, RbacResourceCacheManager cacheManager, LockFactory lockFactory) {
        this(delegate, cacheManager, lockFactory, Duration.ofMinutes(3));
    }

    @Override
    public Set<RbacResource.Permission> getPermissions() {
        Map<String, RbacResource.Permission> result = getPermissionCache();
        return result.isEmpty() ? delegate.getPermissions() : ImmutableSet.copyOf(result.values());
    }

    @Override
    public Set<RbacResource.Role> getRoles() {
        Map<String, RbacResource.Role> result = getRoleCache();
        return result.isEmpty() ? delegate.getRoles() : ImmutableSet.copyOf(result.values());
    }

    @Override
    public Map<String, Set<String>> getUserRoles() {
        return getUserRoleCache();
    }

    public void startScheduleRefreshCache() {
        log.info("start schedule refresh cache task");
        refresh();
    }

    @Override
    public void destroy() throws Exception {
        schedule.shutdown();
    }

    @Override
    public void onApplicationEvent(RbacResourceChangeEvent event) {
        log.info("refresh rbac cache , type = {} , ids = {}", event.getResourceType().getName(), event.getResourceIds());
        if (event.getResourceType() == RbacResource.Permission.class) {
            refreshPermissionCache();
        }

        if (event.getResourceType() == RbacResource.Role.class) {
            refreshRoleCache();
        }

        if (event.getResourceType() == RbacResource.User.class) {
            // TODO 单个用户刷新
            refreshUserRoleCache();
        }
    }

    private void scheduleRefresh() {
        schedule.schedule(this::refresh, refreshInterval.getSeconds(), TimeUnit.SECONDS);
    }

    private void refresh() {
        log.debug("begin refresh rbac resource");
        Lock lock = lockFactory.apply(REFRESH_RBAC_CACHE_LOCK_KEY);
        try {
            if (lock.tryLock(300, TimeUnit.MICROSECONDS)) {
                try {
                    refreshPermissionCache();
                    refreshRoleCache();
                    refreshUserRoleCache();
                } finally {
                    lock.unlock();
                }
            }
            log.debug("refresh rbac resource end");
        } catch (Throwable exception) {
            log.error("refresh rbac resource error, message = {}", exception.getMessage(), exception);
        } finally {
            scheduleRefresh();
        }
    }

    private void refreshUserRoleCache() {
        // 刷新用户角色缓存
        cacheManager.store(RBAC_USER_ROLE_CACHE_NAME, delegate.getUserRoles());
    }

    private void refreshPermissionCache() {
        Map<String, RbacResource.Permission> permissions = delegate.getPermissions()
                .stream()
                .collect(Collectors.toMap(RbacResource::getId, Function.identity()));
        cacheManager.store(RBAC_PERMISSION_CACHE_NAME, permissions);
    }

    private void refreshRoleCache() {
        Map<String, RbacResource.Role> roles = delegate.getRoles()
                .stream()
                .collect(Collectors.toMap(RbacResource::getId, Function.identity()));
        cacheManager.store(RBAC_ROLE_CACHE_NAME, roles);
    }

    @Nonnull
    private Map<String, RbacResource.Permission> getPermissionCache() {
        return requireCache(RBAC_PERMISSION_CACHE_NAME);
    }

    /**
     * @return 角色缓存，且维护角色权限之间的关系
     */
    @Nonnull
    private Map<String, RbacResource.Role> getRoleCache() {
        return requireCache(RBAC_ROLE_CACHE_NAME);
    }

    /**
     * 用户拥有的角色缓存
     */
    @Nonnull
    private Map<String, Set<String>> getUserRoleCache() {
        return requireCache(RBAC_USER_ROLE_CACHE_NAME);
    }

    @NonNull
    @SuppressWarnings("unchecked")
    private <T> Map<String, T> requireCache(String cacheName) {
        Map<String, Object> result = cacheManager.load(cacheName);
        AssertUtils.notNull(result, String.format("获取 Cache 失败，CacheName = %s", cacheName));
        return (Map<String, T>) result;
    }

}
