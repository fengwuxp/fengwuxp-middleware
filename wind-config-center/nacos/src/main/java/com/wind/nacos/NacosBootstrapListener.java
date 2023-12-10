package com.wind.nacos;


import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.spring.context.config.ConfigurationBeanBinder;
import com.alibaba.spring.context.config.DefaultConfigurationBeanBinder;
import com.google.common.base.CaseFormat;
import com.wind.common.WindConstants;
import com.wind.common.exception.AssertUtils;
import com.wind.configcenter.core.ConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.cloud.bootstrap.BootstrapApplicationListener;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.env.PropertySourcesPropertyResolver;
import org.springframework.core.env.SystemEnvironmentPropertySource;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.core.env.StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME;
import static org.springframework.core.env.StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME;

/**
 * 在 bootstrap 阶段向容器注入 nacos 相关 bean
 *
 * @author wuxp
 * @date 2023-10-18 13:16
 **/
@Slf4j
public class NacosBootstrapListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent>, Ordered {

    static final AtomicReference<ConfigService> CONFIG_SERVICE = new AtomicReference<>();

    static final AtomicReference<ConfigRepository> CONFIG_REPOSITORY = new AtomicReference<>();

    @Override
    public void onApplicationEvent(@Nonnull ApplicationEnvironmentPreparedEvent event) {
        if (CONFIG_SERVICE.get() == null) {
            log.info("init nacos bean on bootstrap");
            NacosConfigProperties properties = createNacosProperties(event.getEnvironment());
            AssertUtils.notNull(properties, String.format("please check %s config", NacosConfigProperties.PREFIX));
            CONFIG_SERVICE.set(buildConfigService(properties));
            CONFIG_REPOSITORY.set(new NacosConfigRepository(CONFIG_SERVICE.get(), properties));
        }
        ConfigurableBootstrapContext context = event.getBootstrapContext();
        context.registerIfAbsent(ConfigService.class, c -> CONFIG_SERVICE.get());
        context.registerIfAbsent(ConfigRepository.class, c -> CONFIG_REPOSITORY.get());
    }

    private ConfigService buildConfigService(NacosConfigProperties properties) {
        try {
            return NacosFactory.createConfigService(properties.assembleConfigServiceProperties());
        } catch (NacosException exception) {
            throw new BeanCreationException("create Nacos ConfigService error", exception);
        }
    }

    @Override
    public int getOrder() {
        return BootstrapApplicationListener.DEFAULT_ORDER + 3;
    }

    private NacosConfigProperties createNacosProperties(ConfigurableEnvironment environment) {
        ConfigurationBeanBinder binder = new DefaultConfigurationBeanBinder();
        NacosConfigProperties result = new NacosConfigProperties();
        binder.bind(getNacosConfigs(environment), true, true, result);
        result.setEnvironment(environment);
        return result;
    }

    @Nonnull
    private Map<String, Object> getNacosConfigs(ConfigurableEnvironment environment) {
        // 复制一份
        MutablePropertySources sources = new MutablePropertySources(environment.getPropertySources());
        if (!sources.contains(SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME)) {
            // 如果不存在系统配置，手动加载
            sources.addLast(new MapPropertySource(SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME, environment.getSystemProperties()));
            sources.addLast(new SystemEnvironmentPropertySource(SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, environment.getSystemEnvironment()));
        }
        Map<String, Object> properties = new HashMap<>(environment.getSystemProperties());
        Map<String, Object> result = new HashMap<>();
        properties.putAll(environment.getSystemEnvironment());
        PropertyResolver resolver = new PropertySourcesPropertyResolver(sources);
        properties.forEach((key, val) -> {
            if (key != null && key.startsWith(NacosConfigProperties.PREFIX)) {
                String name = key.substring(NacosConfigProperties.PREFIX.length() + 1);
                // 中划线转驼峰
                name = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name.replace(WindConstants.DASHED, WindConstants.UNDERLINE));
                result.put(name, resolver.getProperty(key, String.class));
            }
        });
        return result;
    }

}
