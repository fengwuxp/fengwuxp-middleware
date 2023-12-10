package com.wind.server.configcenter;


import com.wind.configcenter.core.ConfigRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.lang.NonNull;

import static com.wind.common.WindConstants.WIND_SERVER_CONFIG_CENTER_PROPERTIES_PREFIX;

/**
 * 从配置中心加载配置入口
 *
 * @author wuxp
 * @date 2023-10-18 12:55
 * @see org.springframework.boot.context.logging.LoggingApplicationListener
 **/
@Slf4j
@AllArgsConstructor
public class WindPropertySourceInitializer implements ApplicationListener<ApplicationEnvironmentPreparedEvent>, Ordered {

    @Override
    public void onApplicationEvent(@NonNull ApplicationEnvironmentPreparedEvent event) {
        WindPropertySourceLoader locator = getWindPropertySourceLocator(event);
        if (locator == null) {
            return;
        }
        log.info("load config center properties");
        locator.loadGlobalConfigs(event.getEnvironment());
        locator.loadConfigs(event.getEnvironment());
    }

    private WindPropertySourceLoader getWindPropertySourceLocator(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
        ConfigurableBootstrapContext context = event.getBootstrapContext();
        try {
            WindConfigCenterProperties properties = new WindConfigCenterProperties();
            Binder.get(environment).bind(WIND_SERVER_CONFIG_CENTER_PROPERTIES_PREFIX, Bindable.ofInstance(properties));
            return new WindPropertySourceLoader(context.get(ConfigRepository.class), properties);
        } catch (IllegalStateException e) {
            log.info("unsupported config center");
        }
        return null;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 50;
    }
}
