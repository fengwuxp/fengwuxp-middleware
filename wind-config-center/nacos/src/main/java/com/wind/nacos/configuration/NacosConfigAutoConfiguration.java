package com.wind.nacos.configuration;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.wind.nacos.NacosConfigProperties;
import com.wind.nacos.NacosConfigRepository;
import com.wind.nacos.refresh.NacosContextRefresher;
import com.wind.nacos.refresh.NacosRefreshHistory;
import com.wind.nacos.refresh.SmartConfigurationPropertiesRebinder;
import com.wind.nacos.refresh.condition.ConditionalOnNonDefaultBehavior;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.cloud.context.properties.ConfigurationPropertiesBeans;
import org.springframework.cloud.context.properties.ConfigurationPropertiesRebinder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

import static com.wind.common.WindConstants.ENABLED_NAME;
import static com.wind.common.WindConstants.TRUE;

/**
 * @author juven.xuxb
 * @author freeman
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = NacosConfigProperties.PREFIX, name = ENABLED_NAME, havingValue = TRUE, matchIfMissing = true)
public class NacosConfigAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean(value = NacosConfigProperties.class, search = SearchStrategy.CURRENT)
    public NacosConfigProperties nacosConfigProperties(@NonNull ApplicationContext context) {
        if (context.getParent() != null && BeanFactoryUtils.beanNamesForTypeIncludingAncestors(context.getParent(), NacosConfigProperties.class).length > 0) {
            return BeanFactoryUtils.beanOfTypeIncludingAncestors(context.getParent(), NacosConfigProperties.class);
        }
        return new NacosConfigProperties();
    }

    @Bean
    public NacosRefreshHistory nacosRefreshHistory() {
        return new NacosRefreshHistory();
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
