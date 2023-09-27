package com.wind.security.authority.rbac;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.wind.security.core.rbac.RbacResource;
import com.wind.security.core.rbac.RbacResourceChangeEvent;
import com.wind.security.core.rbac.RbacResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author wuxp
 * @date 2023-09-26 08:31
 **/
@Slf4j
public class WebRbacResourceManager implements ApplicationListener<RbacResourceChangeEvent> {

    private final RbacResourceService rbacResourceService;

    /**
     * 缓存刷新间隔
     */
    private final Duration cacheRefreshInterval;

    /**
     * 权限缓存
     *
     * @key 权限表示
     * @value 权限匹配器
     */
    private final Cache<String, Set<AntPathRequestMatcher>> permissionCaches;

    /**
     * 角色权限关系缓存
     *
     * @key 角色标识
     * @key 角色有的权限
     */
    private final Cache<String, Set<String>> rolePermissionCaches;

    /**
     * 用户角色
     *
     * @key 用户 id
     * @key 用户拥有的角色
     */
    private final Cache<String, Set<String>> userRoleCaches;

    private final ScheduledThreadPoolExecutor scheduledExecutor = new ScheduledThreadPoolExecutor(1, new CustomizableThreadFactory("web-rbac-resource-manager"));

    public WebRbacResourceManager(RbacResourceService rbacResourceService, Duration cacheRefreshInterval) {
        this.rbacResourceService = rbacResourceService;
        this.cacheRefreshInterval = cacheRefreshInterval;
        this.permissionCaches = buildRolesCaches(cacheRefreshInterval);
        this.rolePermissionCaches = buildRolesCaches(cacheRefreshInterval);
        this.userRoleCaches = buildRolesCaches(cacheRefreshInterval);
    }

    /**
     * 获取请求权限匹配器
     *
     * @return {
     * @key 权限表示
     * @key 请求权限匹配器
     * }
     */
    public Map<String, Set<AntPathRequestMatcher>> getRequestPermissionMatchers() {
        return permissionCaches.asMap();
    }

    /**
     * 通过权限标识查找权限
     *
     * @param permissionIds 权限标识
     * @return 有 {@param permissionIds} 权限的角色
     */
    public Set<String> getRolesByPermissionIds(String... permissionIds) {
        Set<String> result = Arrays.stream(permissionIds)
                .map(this::findRole)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        return Collections.unmodifiableSet(result);
    }

    /**
     * 通过用户 id 获取角色
     *
     * @param userId 用户 ID
     * @return 角色列表
     */
    public Set<String> getUserRoles(String userId) {
        Set<String> result = userRoleCaches.get(userId, rbacResourceService::findRolesByUserId);
        return result == null ? Collections.emptySet() : Collections.unmodifiableSet(result);
    }

    private String findRole(String permissionId) {
        for (Map.Entry<String, Set<String>> entry : rolePermissionCaches.asMap().entrySet()) {
            Set<String> permissions = entry.getValue();
            if (permissions.contains(permissionId)) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public void onApplicationEvent(@NonNull RbacResourceChangeEvent event) {
        log.info("refresh rbac cache , type = {} , ids = {}", event.getResourceType().getName(), event.getResourceIds());
        if (event.getResourceType() == RbacResource.Permission.class) {
            // 权限内容变更
            event.getResourceIds().forEach(id -> {
                RbacResource.Permission permission = rbacResourceService.findPermissionById(id);
                if (permission == null) {
                    permissionCaches.invalidate(id);
                } else {
                    putPermissionCaches(permission);
                }
            });
        }
        if (event.getResourceType() == RbacResource.Role.class) {
            // 角色关联的权限变更
            event.getResourceIds().forEach(id -> {
                RbacResource.Role role = rbacResourceService.findRoleById(id);
                if (role == null) {
                    rolePermissionCaches.invalidate(id);
                } else {
                    putRoleCaches(role);
                }
            });
        }

        if (event.getResourceType() == RbacResource.User.class) {
            // 用户关联的角色变更
            event.getResourceIds().forEach(userRoleCaches::invalidate);
        }
    }

    private void scheduleRefresh(long delay) {
        scheduledExecutor.schedule(this::refreshRefresh, delay, TimeUnit.SECONDS);
    }

    private void refreshRefresh() {
        log.debug("begin refresh rbac resource");
        try {
            rbacResourceService.getAllPermissions().forEach(this::putPermissionCaches);
            rbacResourceService.getAllRoles().forEach(this::putRoleCaches);
            log.debug("begin refresh rbac resource end");
        } catch (Exception exception) {
            log.error("refresh rbac resource error, message = {}", exception.getMessage(), exception);
        } finally {
            scheduleRefresh(cacheRefreshInterval.getSeconds());
        }
    }

    private void putPermissionCaches(RbacResource.Permission permission) {
        permissionCaches.put(permission.getId(), convertMatchers(permission.getAttributes()));
    }

    private void putRoleCaches(RbacResource.Role role) {
        rolePermissionCaches.put(role.getId(), role.getPermissions());
    }

    private Set<AntPathRequestMatcher> convertMatchers(Set<String> patterns) {
        return patterns
                .stream()
                .map(pattern -> {
                    if (pattern.contains(" ")) {
                        String[] parts = pattern.split(" ");
                        return new AntPathRequestMatcher(parts[1], parts[0]);
                    }
                    return new AntPathRequestMatcher(pattern);
                })
                .collect(Collectors.toSet());
    }


    private <V> Cache<String, V> buildRolesCaches(Duration cacheEffectiveTime) {
        return Caffeine.newBuilder()
                .executor(scheduledExecutor)
                // 设置最后一次写入或访问后经过固定时间过期
                .expireAfterWrite(cacheEffectiveTime.getSeconds() + 10, TimeUnit.SECONDS)
                // 初始的缓存空间大小
                .initialCapacity(100)
                // 缓存的最大条数
                .maximumSize(1000)
                .build();
    }

}
