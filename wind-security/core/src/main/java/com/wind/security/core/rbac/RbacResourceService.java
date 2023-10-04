package com.wind.security.core.rbac;

import org.springframework.lang.Nullable;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * rbac 资源操作
 *
 * @author wuxp
 * @date 2023-09-25 15:41
 **/
public interface RbacResourceService<I extends Serializable> {

    /**
     * @return 获取所有的 rbac 权限
     * @key 权限 id
     * @value 权限内容
     */
    @NotEmpty
    List<RbacResource.Permission<I>> getAllPermissions();

    /**
     * @param permissionId 权限 id
     * @return 权限
     */
    @Nullable
    RbacResource.Permission<I> findPermissionById(String permissionId);

    /**
     * @return 获取所有的 rbac 角色
     * @key 权限 id
     * @value 权限内容
     */
    @NotEmpty
    List<RbacResource.Role<I>> getAllRoles();

    /**
     * @param roleId 角色 id
     * @return 角色
     */
    @Nullable
    RbacResource.Role<I> findRoleById(String roleId);

    /**
     * 获取用户拥有的角色
     * 如果系统中有多种类型的用户 {@param userId}，可以使用 {type}_{id} 组合
     *
     * @param userId 用户唯一标识
     * @return 角色 ID 列表
     */
    @NotEmpty()
    Set<I> findRolesByUserId(String userId);

}
