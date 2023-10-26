package com.wind.security.authority.rbac;

import com.wind.common.exception.AssertUtils;
import com.wind.security.core.rbac.RbacResource;
import com.wind.security.core.rbac.RbacResourceCache;
import com.wind.security.core.rbac.RbacResourceCacheSupplier;
import com.wind.security.core.rbac.RbacResourceChangeEvent;
import com.wind.security.core.rbac.RbacResourceService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.wind.security.WebSecurityConstants.RBAC_PERMISSION_CACHE_NAME;
import static com.wind.security.WebSecurityConstants.RBAC_ROLE_CACHE_NAME;
import static com.wind.security.WebSecurityConstants.RBAC_USER_ROLE_CACHE_NAME;

/**
 * 基于缓存的 RbacResourceService
 *
 * @author wuxp
 * @date 2023-10-22 09:50
 **/
@Slf4j
@AllArgsConstructor
public class WebRbacResourceService implements RbacResourceService, ApplicationListener<RbacResourceChangeEvent>, InitializingBean {

    private final RbacResourceCacheSupplier cacheSupplier;

    private final RbacResourceService delegate;

    /**
     * 缓存刷新间隔
     */
    private final Duration cacheRefreshInterval;

    private final ScheduledExecutorService schedule = new ScheduledThreadPoolExecutor(1, new CustomizableThreadFactory("web-rbac-resource-refresher"));

    public WebRbacResourceService(RbacResourceCacheSupplier cacheSupplier, RbacResourceService delegate) {
        this(cacheSupplier, delegate, Duration.ofMinutes(3));
    }

    @Override
    public List<RbacResource.Permission> getAllPermissions() {
        return getPermissionCache().values().stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public List<RbacResource.Role> getAllRoles() {
        return getRoleCache().values().stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Nullable
    @Override
    public RbacResource.Permission findPermissionById(String permissionId) {
        return getPermissionCache().computeIfAbsent(permissionId, delegate::findPermissionById);
    }

    @Override
    public List<RbacResource.Permission> findPermissionByIds(Collection<String> permissionIds) {
        return getPermissionCache().getAll(permissionIds);
    }

    @Nullable
    @Override
    public RbacResource.Role findRoleById(String roleId) {
        return getRoleCache().computeIfAbsent(roleId, delegate::findRoleById);
    }

    @Nullable
    @Override
    public List<RbacResource.Role> findRoleByIds(Collection<String> roleIds) {
        return getRoleCache().getAll(roleIds);
    }

    @Override
    public Set<RbacResource.Role> findRolesByUserId(String userId) {
        return getUserRoleCache().computeIfAbsent(userId, delegate::findRolesByUserId);
    }

    @Override
    public void onApplicationEvent(@NonNull RbacResourceChangeEvent event) {
        log.info("refresh rbac cache , type = {} , ids = {}", event.getResourceType().getName(), event.getResourceIds());
        if (event.getResourceType() == RbacResource.Permission.class) {
            if (event.isDeleted()) {
                // 权限删除
                getPermissionCache().removeAll(event.getResourceIds());
            } else {
                // 权限内容变更
                delegate.findPermissionByIds(event.getResourceIds()).forEach(this::putPermissionCaches);
            }
        }

        if (event.getResourceType() == RbacResource.Role.class) {
            if (event.isDeleted()) {
                // 角色删除
                getRoleCache().removeAll(event.getResourceIds());
            } else {
                // 角色内容变更
                delegate.findRoleByIds(event.getResourceIds()).forEach(this::putRoleCaches);
            }
        }

        if (event.getResourceType() == RbacResource.User.class) {
            if (event.isDeleted()) {
                // 用户角色删除
                getUserRoleCache().removeAll(event.getResourceIds());
            } else {
                // 用户角色内容变更
                event.getResourceIds().forEach(id -> getUserRoleCache().put(id, delegate.findRolesByUserId(id)));
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        refreshRefresh();
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
        RbacResourceCache<String, Object> result = cacheSupplier.apply(cacheName, schedule);
        AssertUtils.notNull(result, String.format("获取 Cache 失败，CacheName = %s", cacheName));
        return (RbacResourceCache<String, T>) result;
    }

    @Nonnull
    private RbacResourceCache<String, RbacResource.Permission> getPermissionCache() {
        return requiredCache(RBAC_PERMISSION_CACHE_NAME);
    }

    @Nonnull
    private RbacResourceCache<String, RbacResource.Role> getRoleCache() {
        return requiredCache(RBAC_ROLE_CACHE_NAME);
    }

    @Nonnull
    private RbacResourceCache<String, Set<RbacResource.Role>> getUserRoleCache() {
        return requiredCache(RBAC_USER_ROLE_CACHE_NAME);
    }

    private void putPermissionCaches(RbacResource.Permission permission) {
        getPermissionCache().put(permission.getId(), permission);
    }

    private void putRoleCaches(RbacResource.Role role) {
        getRoleCache().put(role.getId(), role);
    }

}
