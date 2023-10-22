package com.wind.security.authority.rbac;

import com.google.common.collect.ImmutableList;
import com.wind.common.exception.AssertUtils;
import com.wind.security.core.rbac.RbacResource;
import com.wind.security.core.rbac.RbacResourceCache;
import com.wind.security.core.rbac.RbacResourceChangeEvent;
import com.wind.security.core.rbac.RbacResourceService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

/**
 * 基于缓存的 RbacResourceService
 *
 * @author wuxp
 * @date 2023-10-22 09:50
 **/
@Slf4j
@AllArgsConstructor
public class WebRbacResourceService implements RbacResourceService, ApplicationListener<RbacResourceChangeEvent> {

    /**
     * rbac 权限缓存名称
     */
    private static final String RBA_PERMISSION_CACHE_NAME = "RBAC_PERMISSION_CACHE";

    /**
     * rbac 角色缓存名称
     */
    private static final String RBA_ROLE_CACHE_NAME = "RBAC_PERMISSION_CACHE";

    /**
     * rbac 用户角色缓存名称
     */
    private static final String RBA_USER_ROLE_CACHE_NAME = "RBAC_USER_ROLE_CACHE";

    private final BiFunction<String, Executor, RbacResourceCache<String, Object>> cacheProvider;

    private final RbacResourceService delegate;

    /**
     * 缓存刷新间隔
     */
    private final Duration cacheRefreshInterval;

    private final ScheduledExecutorService schedule = new ScheduledThreadPoolExecutor(1, new CustomizableThreadFactory("web-rbac-resource-refresher"));

    public WebRbacResourceService(BiFunction<String, Executor, RbacResourceCache<String, Object>> cacheProvider, RbacResourceService delegate) {
        this(cacheProvider, delegate, Duration.ofMinutes(3));
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public List<RbacResource.Permission> getAllPermissions() {
        List result = ImmutableList.copyOf(getPermissionCache().values());
        return (List<RbacResource.Permission>) result;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public List<RbacResource.Role> getAllRoles() {
        List result = ImmutableList.copyOf(getRoleCache().values());
        return (List<RbacResource.Role>) result;
    }

    @Nullable
    @Override
    public RbacResource.Permission findPermissionById(String permissionId) {
        return getPermissionCache().computeIfAbsent(permissionId, delegate::findPermissionById);
    }

    @Nullable
    @Override
    public RbacResource.Role findRoleById(String roleId) {
        return getRoleCache().computeIfAbsent(roleId, delegate::findRoleById);
    }

    @Override
    public Set<String> findRolesByUserId(String userId) {
        return getUserRoleCache().computeIfAbsent(userId, delegate::findRolesByUserId);
    }

    @Override
    public void onApplicationEvent(@NonNull RbacResourceChangeEvent event) {
        log.info("refresh rbac cache , type = {} , ids = {}", event.getResourceType().getName(), event.getResourceIds());
        if (event.getResourceType() == RbacResource.Permission.class) {
            // 权限内容变更
            event.getResourceIds().forEach(id -> {
                RbacResource.Permission permission = delegate.findPermissionById(id);
                if (permission == null) {
                    getPermissionCache().remove(id);
                } else {
                    putPermissionCaches(permission);
                }
            });
        }

        if (event.getResourceType() == RbacResource.Role.class) {
            // 角色内容变更
            event.getResourceIds().forEach(id -> {
                RbacResource.Role role = delegate.findRoleById(id);
                if (role == null) {
                    getRoleCache().remove(id);
                } else {
                    putRoleCaches(role);
                }
            });
        }
        if (event.getResourceType() == RbacResource.User.class) {
            // 用户角色内容变更
            event.getResourceIds().forEach(id -> {
                Set<String> roles = delegate.findRolesByUserId(id);
                if (roles == null) {
                    getUserRoleCache().remove(id);
                } else {
                    getUserRoleCache().put(id, roles);
                }
            });
        }
    }

    private void scheduleRefresh(long delay) {
        schedule.schedule(this::refreshRefresh, delay, TimeUnit.SECONDS);
    }

    private void refreshRefresh() {
        log.debug("begin refresh rbac resource");
        try {
            delegate.getAllPermissions().forEach(this::putPermissionCaches);
            delegate.getAllRoles().forEach(this::putRoleCaches);
            log.debug("refresh rbac resource end");
        } catch (Exception exception) {
            log.error("refresh rbac resource error, message = {}", exception.getMessage(), exception);
        } finally {
            scheduleRefresh(cacheRefreshInterval.getSeconds());
        }
    }

    @NonNull
    @SuppressWarnings("unchecked")
    private <T> RbacResourceCache<String, T> requiredCache(String cacheName) {
        RbacResourceCache<String, Object> result = cacheProvider.apply(cacheName, schedule);
        AssertUtils.notNull(result, String.format("获取 Cache 失败，CacheName = %s", cacheName));
        return (RbacResourceCache<String, T>) result;
    }

    @Nonnull
    private RbacResourceCache<String, RbacResource.Permission> getPermissionCache() {
        return requiredCache(RBA_PERMISSION_CACHE_NAME);
    }

    @Nonnull
    private RbacResourceCache<String, RbacResource.Role> getRoleCache() {
        return requiredCache(RBA_ROLE_CACHE_NAME);
    }

    @Nonnull
    private RbacResourceCache<String, Set<String>> getUserRoleCache() {
        return requiredCache(RBA_USER_ROLE_CACHE_NAME);
    }

    private void putPermissionCaches(RbacResource.Permission permission) {
        getPermissionCache().put(permission.getId(), permission);
    }

    private void putRoleCaches(RbacResource.Role role) {
        getRoleCache().put(role.getId(), role);
    }

}
