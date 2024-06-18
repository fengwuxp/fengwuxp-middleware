package com.wind.rocketmq.configuration;

import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.wind.common.WindConstants;
import com.wind.common.enums.ConfigFileType;
import com.wind.common.enums.WindMiddlewareType;
import com.wind.common.util.ServiceInfoUtils;
import com.wind.configcenter.core.ConfigRepository;
import com.wind.rocketmq.DefaultMqProducerBeanPostProcessor;
import com.wind.sentinel.ConfigCenterSentinelDataSource;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.apache.rocketmq.spring.autoconfigure.RocketMQProperties;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import static com.wind.common.WindConstants.WIND_MIDDLEWARE_SHARE_NAME;

/**
 * @author wuxp
 * @date 2024-06-17 15:55
 **/
@Configuration
@EnableConfigurationProperties(value = {RocketMQProperties.class})
@AutoConfigureAfter(RocketMQAutoConfiguration.class)
@Import(WindRocketMQAutoConfiguration.DefaultMqProducerHookConfiguration.class)
public class WindRocketMQAutoConfiguration {

    @Bean
    @ConditionalOnClass(ConfigRepository.class)
    @ConditionalOnProperty(prefix = RocketMQProperties.PREFIX, name = "enable-flow-control", havingValue = WindConstants.TRUE)
    public ConfigCenterSentinelDataSource<FlowRule> flowRuleConfigCenterDataSource(ConfigRepository repository, Environment environment) {
        // TODO 待优化
        String middlewareShareName = environment.getProperty(WIND_MIDDLEWARE_SHARE_NAME, ServiceInfoUtils.getApplicationName());
        ConfigRepository.ConfigDescriptor descriptor = ConfigRepository.ConfigDescriptor
                .immutable(String.format("%s-rocketmq", middlewareShareName), WindMiddlewareType.SENTINEL.name(), ConfigFileType.JSON);
        return new ConfigCenterSentinelDataSource<>(repository, descriptor, FlowRule.class);
    }


    @Configuration
    public static class DefaultMqProducerHookConfiguration implements ImportBeanDefinitionRegistrar {

        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
            if (!registry.containsBeanDefinition(DefaultMqProducerBeanPostProcessor.class.getName())) {
                registry.registerBeanDefinition(DefaultMqProducerBeanPostProcessor.class.getName(),
                        new RootBeanDefinition(DefaultMqProducerBeanPostProcessor.class));
            }
        }
    }

}
