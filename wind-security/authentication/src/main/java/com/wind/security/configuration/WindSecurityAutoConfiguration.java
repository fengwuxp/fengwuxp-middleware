package com.wind.security.configuration;

import com.wind.security.authentication.jwt.JwtProperties;
import com.wind.security.authentication.jwt.JwtTokenCodec;
import com.wind.security.authority.rbac.CaffeineCacheProvider;
import com.wind.security.authority.rbac.WebRbacResourceService;
import com.wind.security.authority.rbac.WebRbacRoleSecurityMetadataSource;
import com.wind.security.authority.rbac.WindSecurityRbacProperties;
import com.wind.security.core.rbac.RbacResourceService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import static com.wind.common.WindConstants.ENABLED_NAME;
import static com.wind.common.WindConstants.TRUE;

/**
 * @author wuxp
 * @date 2023-09-26 13:00
 **/
@Configuration
@EnableConfigurationProperties(value = {WindSecurityProperties.class})
@ConditionalOnProperty(prefix = WindSecurityProperties.PREFIX, name = ENABLED_NAME, havingValue = TRUE, matchIfMissing = true)
public class WindSecurityAutoConfiguration {

    /**
     * jwt  配置 prefix
     */
    private static final String JWT_PREFIX = WindSecurityProperties.PREFIX + ".jwt";

    /**
     * rbac 配置 prefix
     */
    public static final String RBAC_PREFIX = WindSecurityProperties.PREFIX + ".rbac";

    @Bean
    @ConfigurationProperties(prefix = JWT_PREFIX)
    public JwtProperties jwtProperties() {
        return new JwtProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = RBAC_PREFIX)
    public WindSecurityRbacProperties windSecurityRbacProperties() {
        return new WindSecurityRbacProperties();
    }

    @Bean
    @ConditionalOnBean(JwtProperties.class)
    public JwtTokenCodec jwtTokenCodec(JwtProperties properties) {
        return new JwtTokenCodec(properties);
    }

    @Bean
    @ConditionalOnBean({WindSecurityRbacProperties.class, RbacResourceService.class})
    @ConditionalOnMissingBean({WebRbacResourceService.class})
    @Primary
    public WebRbacResourceService webRbacResourceService(RbacResourceService delegate, WindSecurityRbacProperties properties) {
        return new WebRbacResourceService(new CaffeineCacheProvider(properties.getCacheEffectiveTime()), delegate, properties.getCacheEffectiveTime());
    }

    @Bean
    @ConditionalOnBean({WindSecurityRbacProperties.class, RbacResourceService.class})
    public WebRbacRoleSecurityMetadataSource webRbacRoleSecurityMetadataSource(RbacResourceService rbacResourceService, WindSecurityRbacProperties properties) {
        return new WebRbacRoleSecurityMetadataSource(rbacResourceService, properties.getRolePrefix(), properties.isMatchesRequestAllPermission());
    }

}



