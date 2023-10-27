package com.wind.security.core.rbac;

import com.wind.common.spring.ApplicationContextUtils;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * rbac 资源发生变更
 *
 * @author wuxp
 * @date 2023-09-26 09:57
 **/
@Getter
public class RbacResourceChangeEvent extends ApplicationEvent {

    private static final long serialVersionUID = -4959621536894633454L;

    private final Class<?> resourceType;

    /**
     * 删除操作
     */
    private final boolean deleted;

    private RbacResourceChangeEvent(Collection<String> ids, Class<?> resourceType, boolean deleted) {
        super(ids == null ? Collections.emptyList() : Collections.unmodifiableCollection(ids));
        this.resourceType = resourceType;
        this.deleted = deleted;
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

    public static void refreshPermissionTextIds(Collection<String> ids) {
        publish(new RbacResourceChangeEvent(ids, RbacResource.Permission.class, false));
    }

    public static void refreshRoleTextIds(Collection<String> ids) {
        publish(new RbacResourceChangeEvent(ids, RbacResource.Role.class, false));
    }

    public static void refreshUserRoleTextIds(Collection<String> ids) {
        publish(new RbacResourceChangeEvent(ids, RbacResource.User.class, false));
    }

    /**
     * 删除权限
     *
     * @param permissionIds 权限 id
     */
    public static void removePermissions(Collection<Long> permissionIds) {
        removePermissionTextIds(idAsText(permissionIds));
    }

    /**
     * 删除角色
     *
     * @param roleIds 角色 id
     */
    public static void removeRoles(Collection<Long> roleIds) {
        removeRoleTextIds(idAsText(roleIds));
    }

    /**
     * 删除用户关联的角色
     *
     * @param userIds 用户 id
     */
    public static void removeUserRoles(Collection<Long> userIds) {
        removeUserRoleTextIds(idAsText(userIds));
    }

    public static void removePermissionTextIds(Collection<String> ids) {
        publish(new RbacResourceChangeEvent(ids, RbacResource.Permission.class, true));
    }

    public static void removeRoleTextIds(Collection<String> ids) {
        publish(new RbacResourceChangeEvent(ids, RbacResource.Role.class, true));
    }

    public static void removeUserRoleTextIds(Collection<String> ids) {
        publish(new RbacResourceChangeEvent(ids, RbacResource.User.class, true));
    }

    private static void publish(RbacResourceChangeEvent event) {
        ApplicationContextUtils.getContext().publishEvent(event);
    }

    private static Collection<String> idAsText(Collection<Long> ids) {
        return ids.stream().map(String::valueOf).collect(Collectors.toSet());
    }
}
