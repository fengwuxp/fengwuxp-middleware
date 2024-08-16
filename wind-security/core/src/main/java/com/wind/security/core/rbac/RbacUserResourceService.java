package com.wind.security.core.rbac;

import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * rbac 用户资源服务
 *
 * @author wuxp
 * @date 2023-09-25 15:41
 **/
public interface RbacUserResourceService {

    /**
     * 获取用户角色标识列表
     *
     * @param userId 用户标识
     * @return 角色标识集合
     */
    @NotNull
    Set<String> findOwnerRoleIds(String userId);

    /**
     * 获取用户拥有的角色
     *
     * @param userId 用户 id
     * @return 角色列表
     */
    Set<RbacResource.Role> getOwnerRoles(String userId);

}
