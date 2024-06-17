package com.wind.rocketmq.configuration;

import com.wind.rocketmq.DefaultMqProducerBeanPostProcessor;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author wuxp
 * @date 2024-06-17 15:55
 **/
@AutoConfigureAfter(RocketMQAutoConfiguration.class)
@Import(WIndRocketMQAutoConfiguration.DefaultMqProducerHookConfiguration.class)
public class WIndRocketMQAutoConfiguration {


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
