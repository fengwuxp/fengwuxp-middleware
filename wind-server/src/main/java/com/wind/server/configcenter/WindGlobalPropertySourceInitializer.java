package com.wind.server.configcenter;

import com.wind.common.WindConstants;
import org.springframework.boot.context.logging.LoggingApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

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
        // 仅在 bootstrap 阶段加载全局配置且值加载一次
        if (environment.getPropertySources().contains(WindConstants.GLOBAL_CONFIG_NAME)) {
            return;
        }
        // 由于在 LoggingApplicationListener 之前执行，不会输出日志，使用 jul 输出
        LOGGER.info("load global config");
        loader.loadGlobalConfigs(environment);
    }

    @Override
    public int getOrder() {
        return LoggingApplicationListener.DEFAULT_ORDER - 2;
    }
}
