package com.wind.security.core.rbac;

import org.springframework.lang.Nullable;

import javax.validation.constraints.NotEmpty;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    RbacResource.Permission findPermissionById(String permissionId);

    /**
     * @param permissionIds 权限 id 集合
     * @return 权限集合
     */
    @NotEmpty()
    default List<RbacResource.Permission> findPermissionByIds(Collection<String> permissionIds) {
        if (permissionIds == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(permissionIds.stream().map(this::findPermissionById).collect(Collectors.toList()));
    }

    /**
     * @param roleId 角色 id
     * @return 角色
     */
    @Nullable
    RbacResource.Role findRoleById(String roleId);

    /**
     * @param roleIds 角色 ids
     * @return 角色
     */
    @NotEmpty()
    default List<RbacResource.Role> findRoleByIds(Collection<String> roleIds) {
        if (roleIds == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(roleIds.stream().map(this::findRoleById).collect(Collectors.toList()));
    }

    /**
     * 获取用户拥有的角色
     * 如果系统中有多种类型的用户 {@param userId}，可以使用 {type}_{id} 组合
     *
     * @param userId 用户唯一标识
     * @return 角色唯一标识
     */
    @NotEmpty()
    Set<RbacResource.Role> findRolesByUserId(String userId);

    /**
     * @return 获取已登录用户 id
     */
    default Set<String> getAuthenticatedUserIds() {
        return Collections.emptySet();
    }
}
