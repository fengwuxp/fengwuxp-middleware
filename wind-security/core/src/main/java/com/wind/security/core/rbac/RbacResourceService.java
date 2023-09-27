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
public interface RbacResourceService {

    /**
     * @return 获取所有的 rbac 权限
     * @key 权限 id
     * @value 权限内容
     */
    @NotEmpty
    List<RbacResource.Permission> getAllPermissions();

    /**
     * @param permissionId 权限 id
     * @return 权限
     */
    @Nullable
    RbacResource.Permission findPermissionById(Serializable permissionId);

    /**
     * @return 获取所有的 rbac 角色
     * @key 权限 id
     * @value 权限内容
     */
    @NotEmpty
    List<RbacResource.Role> getAllRoles();

    /**
     * @param roleId 角色 id
     * @return 角色
     */
    @Nullable
    RbacResource.Role findRoleById(Serializable roleId);

    /**
     * 获取用户拥有的角色
     *
     * @param userId 用户 id
     * @return 角色列表
     */
    @NotEmpty()
    Set<String> findRolesByUserId(Serializable userId);

}
