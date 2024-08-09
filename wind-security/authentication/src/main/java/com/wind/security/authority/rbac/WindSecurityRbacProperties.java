package com.wind.security.authority.rbac;

import lombok.Data;

import java.time.Duration;

/**
 * rbac 相关配置
 *
 * @author wuxp
 * @date 2023-09-26 13:00
 **/
@Data
public class WindSecurityRbacProperties {

    /**
     * rbac resource 缓存有效期
     */
    private Duration cacheEffectiveTime = Duration.ofMinutes(3);

    /**
     * 开启缓存刷新
     * 默认：不开启
     *
     * @see com.wind.security.core.rbac.RbacResourceChangeEvent
     */
    private boolean enableRefreshCache = false;

    /**
     * 默认的角色值前缀
     *
     * @see org.springframework.security.access.vote.RoleVoter#getRolePrefix
     */
    private String rolePrefix = "ROLE_";

    /**
     * 请求权限匹配是否匹配所有权限
     */
    private boolean matchesRequestAllPermission = true;

}
