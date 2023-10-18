package com.wind.server.configcenter;

import com.wind.common.WindConstants;
import com.wind.configcenter.core.ConfigRepository;
import com.wind.configcenter.core.ConfigRepository.ConfigDescriptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.logging.LoggingApplicationListener;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.PropertySource;

import java.util.List;

/**
 * 在 bootstrap 阶段加载全局配置
 *
 * @author wuxp
 * @date 2023-10-18 12:55
 **/
@Slf4j
public class WindGlobalPropertyLoader implements ApplicationListener<ApplicationEnvironmentPreparedEvent>, Ordered {

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        if (event.getEnvironment().getPropertySources().contains(WindConstants.GLOBAL_CONFIG_NAME)) {
            return;
        }
        ConfigRepository repository = getConfigRepository(event);
        if (repository == null) {
            return;
        }
        log.info("load global config");
        CompositePropertySource result = new CompositePropertySource(WindConstants.GLOBAL_CONFIG_NAME);
        ConfigDescriptor descriptor = ConfigDescriptor.immutable(WindConstants.GLOBAL_CONFIG_NAME, WindConstants.GLOBAL_CONFIG_GROUP);
        List<PropertySource<?>> configs = repository.getConfigs(descriptor);
        configs.forEach(result::addFirstPropertySource);
        event.getEnvironment().getPropertySources().addLast(result);
    }

    private ConfigRepository getConfigRepository(ApplicationEnvironmentPreparedEvent event) {
        try {
            return event.getBootstrapContext().get(ConfigRepository.class);
        } catch (IllegalStateException e) {
            log.info("un enabled config center supported");
        }
        return null;
    }

    @Override
    public int getOrder() {
        return LoggingApplicationListener.DEFAULT_ORDER - 10;
    }
}
