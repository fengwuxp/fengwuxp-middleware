package com.wind.server.configcenter;

import com.wind.common.WindConstants;
import com.wind.common.enums.ConfigFileType;
import com.wind.common.enums.WindMiddlewareType;
import com.wind.common.exception.AssertUtils;
import com.wind.configcenter.core.ConfigRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.wind.common.WindConstants.SPRING_APPLICATION_NAME;
import static com.wind.common.WindConstants.WIND_SERVER_USED_MIDDLEWARE;

/**
 * 配置分为应用配置和中间件配置，加载规则如下
 * 应用配置名称：{spring.application.name}.properties  配置分组：APP
 * 中间件配置：{spring.application.name or 自定名称}-{middlewareType}.toLowerCase().properties  配置分组：middlewareType
 * 中间件配置名称：wind.{middlewareType}.name=xxx
 * middlewareType {@link WindMiddlewareType}
 * 不同应用如果需要共享一份配置，可以将 wind.{middlewareType}.name 配置成一致的
 *
 * @author wuxp
 * @date 2023-10-15 12:30
 **/
@AllArgsConstructor
@Slf4j
public class WindPropertySourceLocator implements PropertySourceLocator {

    private final ConfigRepository repository;

    /**
     * 额外需要加载的配置
     */
    private final Collection<SimpleConfigDescriptor> configDescriptors;

    private final ConfigFileType fileExtension;

    @Override
    public PropertySource<?> locate(Environment environment) {
        CompositePropertySource result = new CompositePropertySource(repository.getConfigSourceName());
        // 加载全局配置
        loadConfigs(buildDescriptor(WindConstants.GLOBAL_CONFIG_NAME, WindConstants.GLOBAL_CONFIG_GROUP), result);
        String applicationName = environment.getProperty(SPRING_APPLICATION_NAME);
        AssertUtils.notNull(applicationName, SPRING_APPLICATION_NAME + " must not empty");
        // 加载中间件配置
        for (WindMiddlewareType type : getUsedMiddlewareTypes(environment)) {
            String name = environment.getProperty(type.getConfigName(), applicationName);
            AssertUtils.notNull(name, type.getConfigName() + " must not empty");
            loadConfigs(buildDescriptor(name + WindConstants.DASHED + type.name().toLowerCase(), type.name()), result);
        }
        // 加载应用配置
        loadConfigs(buildDescriptor(applicationName, WindConstants.APP_CONFIG_GROUP), result);
        // 加载额外的自定义配置
        configDescriptors.forEach(descriptor -> loadConfigs(descriptor, result));
        return result;
    }

    private static List<WindMiddlewareType> getUsedMiddlewareTypes(Environment environment) {
        String config = environment.getProperty(WIND_SERVER_USED_MIDDLEWARE);
        if (StringUtils.hasLength(config)) {
            return Arrays.stream(config.split(WindConstants.COMMA)).map(WindMiddlewareType::valueOf).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public Collection<PropertySource<?>> locateCollection(Environment environment) {
        return PropertySourceLocator.super.locateCollection(environment);
    }

    private SimpleConfigDescriptor buildDescriptor(String name, String group) {
        SimpleConfigDescriptor result = SimpleConfigDescriptor.of(name, group);
        if (result.getFileType() == null) {
            result.setFileType(fileExtension);
        }
        return result;
    }

    private void loadConfigs(ConfigRepository.ConfigDescriptor descriptor, CompositePropertySource result) {
        if (log.isDebugEnabled()) {
            log.debug("load config，id = {}, group = {}, refreshable = {}", descriptor.getConfigId(), descriptor.getGroup(), descriptor.isRefreshable());
        }
        List<PropertySource<?>> configs = repository.getConfigs(descriptor);
        configs.forEach(result::addFirstPropertySource);
    }
}
