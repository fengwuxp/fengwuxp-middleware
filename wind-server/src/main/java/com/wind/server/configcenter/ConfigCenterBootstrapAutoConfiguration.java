package com.wind.server.configcenter;

import com.wind.configcenter.core.ConfigRepository;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.MapPropertySource;

import java.util.Collections;

import static com.wind.common.WindConstants.WIND_SERVER_CONFIG_CENTER_PROPERTIES_PREFIX;

/**
 * 配置中心相关
 *
 * @author wuxp
 * @date 2023-10-15 16:42
 **/
@Configuration
@ConditionalOnProperty(prefix = WIND_SERVER_CONFIG_CENTER_PROPERTIES_PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class ConfigCenterBootstrapAutoConfiguration {
    @Bean
    @ConfigurationProperties(prefix = WIND_SERVER_CONFIG_CENTER_PROPERTIES_PREFIX)
    @ConditionalOnMissingBean(WindConfigCenterProperties.class)
    public WindConfigCenterProperties windConfigCenterProperties() {
        return new WindConfigCenterProperties();
    }

    @Bean
    @ConditionalOnClass(name = "com.wind.nacos.configuration.NacosConfigBootstrapConfiguration")
    @ConditionalOnMissingBean(name = "windPropertySourceLocator")
    public PropertySourceLocator windPropertySourceLocator(ApplicationContext context, WindConfigCenterProperties properties) {
        try {
            // 由于在 bootstrap 阶段 ConditionalOnBean 未生效，改为手动处理
            ConfigRepository repository = context.getBean(ConfigRepository.class);
            return new WindPropertySourceLocator(repository, properties);
        } catch (BeansException e) {
            return environment -> new MapPropertySource("empty", Collections.emptyMap());
        }
    }


}
