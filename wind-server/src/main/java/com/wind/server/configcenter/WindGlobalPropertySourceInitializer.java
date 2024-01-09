package com.wind.server.configcenter;

import org.springframework.boot.context.logging.LoggingApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

import static org.springframework.cloud.bootstrap.BootstrapApplicationListener.BOOTSTRAP_PROPERTY_SOURCE_NAME;

/**
 * 从配置中心加载全局配置
 *
 * @author wuxp
 * @date 2023-12-29 11:26
 * @see org.springframework.boot.context.logging.LoggingApplicationListener
 **/
public class WindGlobalPropertySourceInitializer extends WindAbstractPropertySourceInitializer {

    private static final Logger LOGGER = Logger.getLogger(WindGlobalPropertySourceInitializer.class.getName());

    static {
        LOGGER.addHandler(new ConsoleHandler());
    }

    @Override
    protected void load(WindPropertySourceLoader loader, ConfigurableEnvironment environment) {
        // don't listen to events in a bootstrap context
        if (environment.getPropertySources().contains(BOOTSTRAP_PROPERTY_SOURCE_NAME)) {
            // 由于在 LoggingApplicationListener 之前执行，不会输出日志，使用 jul 输出
            LOGGER.info("load global config");
            loader.loadGlobalConfigs(environment);
        }
    }

    @Override
    public int getOrder() {
        return LoggingApplicationListener.DEFAULT_ORDER - 2;
    }
}
