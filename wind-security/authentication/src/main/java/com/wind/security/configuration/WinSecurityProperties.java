package com.wind.security.configuration;

import com.wind.security.authentication.jwt.JwtProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * @author wuxp
 * @date 2023-09-26 13:00
 **/
@Data
@ConfigurationProperties(prefix = WinSecurityProperties.PREFIX)
public class WinSecurityProperties {

    /**
     * 配置 prefix
     */
    public static final String PREFIX = "wind.security";

    /**
     * 是否启用
     */
    private boolean enabled = true;

    /**
     * jwt
     */
    private JwtProperties jwt;

    /**
     * rbac 缓存失效时间
     */
    private Duration rbacCacheEffectiveTime = Duration.ofMinutes(5);


}
