package com.wind.security.core.rbac;

import com.wind.common.spring.SpringEventPublishUtils;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * rbac 资源变更事件
 *
 * @author wuxp
 * @date 2023-09-26 09:57
 **/
@Getter
public class RbacResourceChangeEvent extends ApplicationEvent {

    private static final long serialVersionUID = -4959621536894633454L;

    private final Class<?> resourceType;

    private RbacResourceChangeEvent(Collection<String> ids, Class<?> resourceType) {
        super(ids == null ? Collections.emptyList() : Collections.unmodifiableCollection(ids));
        this.resourceType = resourceType;
    }

    /**
     * @return rbac 资源 id
     */
    @SuppressWarnings("unchecked")
    public Collection<String> getResourceIds() {
        return (Collection<String>) getSource();
    }

    /**
     * 刷新权限缓存
     *
     * @param ids 权限 id
     */
    public static void refreshPermissions(Collection<Long> ids) {
        refreshPermissionTextIds(idAsText(ids));
    }

    /**
     * 刷新角色缓存
     *
     * @param roleIds 角色 id
     */
    public static void refreshRoles(Collection<Long> roleIds) {
        refreshRoleTextIds(idAsText(roleIds));
    }

    /**
     * 刷新用户关联角色缓存
     *
     * @param userIds 用户 id
     */
    public static void refreshUserRoles(Collection<Long> userIds) {
        refreshUserRoleTextIds(idAsText(userIds));
    }

    // 刷新所有资源
    public static void refreshAll() {
        publish(new RbacResourceChangeEvent(null, RbacResource.class));
    }

    public static void refreshPermissionTextIds(Collection<String> ids) {
        publish(new RbacResourceChangeEvent(ids, RbacResource.Permission.class));
    }

    public static void refreshRoleTextIds(Collection<String> ids) {
        publish(new RbacResourceChangeEvent(ids, RbacResource.Role.class));
    }

    public static void refreshUserRoleTextIds(Collection<String> ids) {
        publish(new RbacResourceChangeEvent(ids, RbacResource.User.class));
    }

    private static void publish(RbacResourceChangeEvent event) {
        SpringEventPublishUtils.publishEvent(event);
    }

    private static Collection<String> idAsText(Collection<Long> ids) {
        return ids.stream().map(String::valueOf).collect(Collectors.toSet());
    }

}
