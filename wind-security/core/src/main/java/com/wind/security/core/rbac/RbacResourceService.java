package com.wind.security.core.rbac;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
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
     */
    @NotNull
    Set<RbacResource.Permission> getPermissions();

    /**
     * 通过角色 id 获取权限列表
     *
     * @param roleIds 角色id列表
     * @return 权限集合
     */
    @NotNull
    Set<RbacResource.Permission> findPermissionsByRoleIds(Collection<String> roleIds);

    /**
     * @return 获取所有的 rbac 角色
     */
    @NotEmpty
    Set<RbacResource.Role> getRoles();

    /**
     * @return 获取所有的用户角缓存
     */
    @NotEmpty
    Map<String, Set<String>> getUserRoles();

    /**
     * 通过角色 id 获取权限列表
     *
     * @param userId 用户标识
     * @return 角色标识集合
     */
    @NotNull
   default Set<String> findOwnerRoleIds(String userId){
        return getUserRoles().getOrDefault(userId, Collections.emptySet());
    }

    /**
     * 获取用户拥有的角色
     *
     * @param userId 用户 id
     * @return 角色列表
     */
    default Set<RbacResource.Role> getOwnerRoles(String userId) {
        Set<String> roleIds = findOwnerRoleIds(userId);
        return getRoles().stream().filter(role -> roleIds.contains(role.getId())).collect(Collectors.toSet());
    }

}
