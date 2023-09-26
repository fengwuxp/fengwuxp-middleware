package com.wind.security.configuration;

import com.wind.security.authentication.jwt.JwtTokenCodec;
import com.wind.security.authority.rbac.WebRbacResourceManager;
import com.wind.security.core.rbac.RbacResourceService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wuxp
 * @date 2023-09-26 13:00
 **/
@Configuration
@EnableConfigurationProperties(value = {WinSecurityProperties.class})
@ConditionalOnBean({CacheManager.class})
@ConditionalOnProperty(prefix = WinSecurityProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class WinSecurityAutoConfiguration {


    @Bean
    @ConditionalOnBean(WinSecurityProperties.class)
    public JwtTokenCodec jwtTokenCodec(WinSecurityProperties properties) {
        return new JwtTokenCodec(properties.getJwt());
    }

    @Bean
    @ConditionalOnBean(WinSecurityProperties.class)
    public WebRbacResourceManager webRbacResourceManager(RbacResourceService rbacResourceService, WinSecurityProperties properties) {
        return new WebRbacResourceManager(rbacResourceService, properties.getRbacCacheEffectiveTime());
    }


}



