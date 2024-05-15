package com.wind.security.core.rbac;

import com.google.common.collect.ImmutableSet;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * rbac 资源操作
 *
 * @author wuxp
 * @date 2023-09-25 15:41
 **/
public interface RbacResourceService {

    /**
     * @return 获取所有的 rbac 权限
     * @key 权限 id
     * @value 权限内容
     */
    @NotEmpty
    List<RbacResource.Permission> getAllPermissions();

    /**
     * @return 获取所有的 rbac 角色
     * @key 权限 id
     * @value 权限内容
     */
    @NotEmpty
    List<RbacResource.Role> getAllRoles();

    /**
     * @param permissionId 权限 id
     * @return 权限
     */
    @Nullable
    default RbacResource.Permission findPermissionById(String permissionId) {
        Iterator<RbacResource.Permission> iterator = findPermissionByIds(Collections.singletonList(permissionId)).iterator();
        return iterator.hasNext() ? iterator.next() : null;
    }

    /**
     * @param permissionIds 权限 id 集合
     * @return 权限集合
     */
    @NotNull
    List<RbacResource.Permission> findPermissionByIds(Collection<String> permissionIds);

    /**
     * @param roleId 角色 id
     * @return 角色
     */
    @Nullable
    default RbacResource.Role findRoleById(String roleId) {
        Iterator<RbacResource.Role> iterator = findRoleByIds(Collections.singletonList(roleId)).iterator();
        return iterator.hasNext() ? iterator.next() : null;
    }

    /**
     * @param roleIds 角色 ids
     * @return 角色
     */
    @NotNull
    List<RbacResource.Role> findRoleByIds(Collection<String> roleIds);

    /**
     * 获取用户拥有的角色标识
     * 如果系统中有多种类型的用户 {@param userId}，可以使用 {type}_{id} 组合
     *
     * @param userId 用户唯一标识
     * @return 角色标识列表
     */
    @NotNull
    Set<String> finUserOwnerRoleIds(String userId);

    /**
     * 获取用户拥有的角色
     * 如果系统中有多种类型的用户 {@param userId}，可以使用 {type}_{id} 组合
     *
     * @param userId 用户唯一标识
     * @return 角色列表
     */
    @NotNull
    default Set<RbacResource.Role> finUserOwnerRoles(String userId) {
        return ImmutableSet.copyOf(findRoleByIds(finUserOwnerRoleIds(userId)));
    }

    /**
     * @return 获取已登录用户 id
     */
    default Set<String> getAuthenticatedUserIds() {
        return Collections.emptySet();
    }
}
