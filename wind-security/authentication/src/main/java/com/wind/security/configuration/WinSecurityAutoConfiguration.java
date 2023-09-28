package com.wind.security.configuration;

import com.wind.security.authentication.jwt.JwtProperties;
import com.wind.security.authentication.jwt.JwtTokenCodec;
import com.wind.security.authority.rbac.WebRbacResourceManager;
import com.wind.security.core.rbac.RbacResourceService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wuxp
 * @date 2023-09-26 13:00
 **/
@Configuration
@EnableConfigurationProperties(value = {WinSecurityProperties.class})
@ConditionalOnProperty(prefix = WinSecurityProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class WinSecurityAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = WinSecurityProperties.PREFIX + ".jwt")
    public JwtProperties jwtProperties() {
        return new JwtProperties();
    }

    @Bean
    @ConditionalOnBean(JwtProperties.class)
    public JwtTokenCodec jwtTokenCodec(JwtProperties properties) {
        return new JwtTokenCodec(properties);
    }

    @Bean
    @ConditionalOnBean(WinSecurityProperties.class)
    public WebRbacResourceManager webRbacResourceManager(RbacResourceService rbacResourceService, WinSecurityProperties properties) {
        return new WebRbacResourceManager(rbacResourceService, properties.getRbacCacheEffectiveTime());
    }


}



