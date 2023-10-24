package com.wind.security.core;

/**
 * 安全的访问操作判断
 *
 * @author wuxp
 * @date 2023-10-24 07:51
 * @see org.springframework.security.access.expression.SecurityExpressionOperations
 **/
public interface SecurityAccessOperations {

    /**
     * 超级管理员角色名称
     */
    String SUPPER_ADMIN_ROLE_NAME = "SUPPER_ADMIN";

    /**
     * 是否有 {@param authority}权限
     *
     * @param authority 权限标识
     * @return 是否有权限
     */
    default boolean hasAuthority(String authority) {
        return hasAnyAuthority(authority);
    }

    /**
     * 是否有 {@param authorities}中的任意一个权限
     *
     * @param authorities 权限标识
     * @return 是否有权限
     */
    boolean hasAnyAuthority(String... authorities);

    /**
     * 是否有 {@param role} 角色
     *
     * @param role 角色标识
     * @return 是否有角色
     */
    default boolean hasRole(String role) {
        return hasAnyRole(role);
    }

    /**
     * 是否有 {@param role}中的任意一个角色
     *
     * @param roles 角色标识
     * @return 是否有角色
     */
    boolean hasAnyRole(String... roles);

    /**
     * @return 是否为超级管理员
     */
    default boolean isSupperAdmin() {
        return hasAnyRole(SUPPER_ADMIN_ROLE_NAME);
    }

}
