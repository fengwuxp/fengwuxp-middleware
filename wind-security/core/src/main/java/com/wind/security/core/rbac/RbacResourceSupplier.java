package com.wind.security.core.rbac;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Set;

/**
 * rbac 资源管提供者
 *
 * @author wuxp
 * @date 2023-10-23 09:47
 **/
public interface RbacResourceSupplier {

    /**
     * @return 获取所有的 rbac 权限
     */
    @NotNull
    Set<RbacResource.Permission> getPermissions();

    /**
     * @return 获取所有的 rbac 角色
     */
    @NotEmpty
    Set<RbacResource.Role> getRoles();

    /**
     * @return 获取所有的用户角缓存
     */
    @NotEmpty
    Map<String, Set<RbacResource.UserRole>> getUserRoles();

}
