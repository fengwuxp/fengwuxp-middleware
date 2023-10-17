package com.wind.nacos.configuration;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.wind.nacos.NacosConfigProperties;
import com.wind.nacos.NacosConfigRepository;
import com.wind.nacos.refresh.SmartConfigurationPropertiesRebinder;
import com.wind.nacos.refresh.condition.ConditionalOnNonDefaultBehavior;
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
 * @author wuxp
 * @date 2023-10-15 14:19
 **/
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = NacosConfigProperties.PREFIX, name = ENABLED_NAME, havingValue = TRUE, matchIfMissing = true)
public class NacosConfigBootstrapConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public NacosConfigProperties nacosConfigProperties() {
        return new NacosConfigProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public ConfigService configService(NacosConfigProperties properties) throws NacosException {
        return NacosFactory.createConfigService(properties.assembleConfigServiceProperties());
    }

    @Bean
    @ConditionalOnMissingBean
    public NacosConfigRepository nacosConfigRepository(ConfigService configService, NacosConfigProperties properties) {
        return new NacosConfigRepository(configService, properties);
    }

    /**
     * Compatible with bootstrap way to start.
     */
    @Bean
    @ConditionalOnMissingBean(search = SearchStrategy.CURRENT)
    @ConditionalOnNonDefaultBehavior
    public ConfigurationPropertiesRebinder smartConfigurationPropertiesRebinder(ConfigurationPropertiesBeans beans) {
        // If using default behavior, not use SmartConfigurationPropertiesRebinder.
        // Minimize te possibility of making mistakes.
        return new SmartConfigurationPropertiesRebinder(beans);
    }

}