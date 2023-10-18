package com.wind.nacos;

import com.alibaba.nacos.api.config.ConfigService;
import com.wind.configcenter.core.ConfigRepository;
import com.wind.nacos.refresh.NacosContextRefresher;
import com.wind.nacos.refresh.NacosRefreshHistory;
import com.wind.nacos.refresh.SmartConfigurationPropertiesRebinder;
import com.wind.nacos.refresh.condition.ConditionalOnNonDefaultBehavior;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.cloud.context.properties.ConfigurationPropertiesBeans;
import org.springframework.cloud.context.properties.ConfigurationPropertiesRebinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.wind.common.WindConstants.ENABLED_NAME;
import static com.wind.common.WindConstants.TRUE;

/**
 * @author juven.xuxb
 * @author freeman
 */
@Configuration(proxyBeanMethods = false)
@AllArgsConstructor
@ConditionalOnProperty(prefix = NacosConfigProperties.PREFIX, name = ENABLED_NAME, havingValue = TRUE, matchIfMissing = true)
public class NacosConfigAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(value = NacosConfigProperties.class, search = SearchStrategy.CURRENT)
    public NacosConfigProperties nacosConfigProperties() {
        return new NacosConfigProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public ConfigService configService() {
        // 使用同一个实例
        return NacosBootstrapListener.CONFIG_SERVICE.get();
    }

    @Bean
    @ConditionalOnMissingBean
    public ConfigRepository nacosConfigRepository() {
        return NacosBootstrapListener.CONFIG_REPOSITORY.get();
    }

    @Bean
    public NacosRefreshHistory nacosRefreshHistory() {
        return new NacosRefreshHistory();
    }

    @Bean
    public NacosContextRefresher nacosContextRefresher(NacosConfigProperties properties, ConfigService configService, NacosRefreshHistory nacosRefreshHistory) {
        // Consider that it is not necessary to be compatible with the previous
        // configuration
        // and use the new configuration if necessary.
        return new NacosContextRefresher(properties, configService, nacosRefreshHistory);
    }

    @Bean
    @ConditionalOnMissingBean(search = SearchStrategy.CURRENT)
    @ConditionalOnNonDefaultBehavior
    public ConfigurationPropertiesRebinder smartConfigurationPropertiesRebinder(ConfigurationPropertiesBeans beans) {
        // If using default behavior, not use SmartConfigurationPropertiesRebinder.
        // Minimize te possibility of making mistakes.
        return new SmartConfigurationPropertiesRebinder(beans);
    }

}
