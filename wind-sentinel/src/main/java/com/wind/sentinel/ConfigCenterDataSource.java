package com.wind.sentinel;

import com.alibaba.csp.sentinel.datasource.AbstractDataSource;
import com.alibaba.fastjson2.JSON;
import com.wind.configcenter.core.ConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.PropertySource;

import java.util.List;

/**
 * 基于配置中心的规则配置数据源
 *
 * @author wuxp
 * @date 2024-03-12 10:04
 **/
@Slf4j
public class ConfigCenterDataSource<T> extends AbstractDataSource<String, List<T>> {

    private final ConfigRepository configRepository;

    private final ConfigRepository.ConfigDescriptor descriptor;

    private final ConfigRepository.ConfigSubscription subscription;

    public ConfigCenterDataSource(ConfigRepository configRepository, ConfigRepository.ConfigDescriptor descriptor, Class<T> configType) {
        super(source -> JSON.parseArray(source, configType));
        this.configRepository = configRepository;
        this.descriptor = descriptor;
        this.subscription = configRepository.onChange(descriptor, new ConfigRepository.ConfigListener() {
            @Override
            public void change(String config) {
                updateConfig(parser.convert(config));
            }

            @Override
            public void change(List<PropertySource<?>> configs) {
                log.debug("unsupported update, please see change(String config) method");
            }
        });
        FlowRuleListenRegister.registerListen(configType, this);
        initConfig();
    }

    @Override
    public String readSource() throws Exception {
        return configRepository.getTextConfig(descriptor);
    }

    @Override
    public void close() throws Exception {
        subscription.unsubscribe();
    }

    private void initConfig() {
        try {
            updateConfig(loadConfig());
        } catch (Exception exception) {
            log.error("load sentinel config exception", exception);
        }
    }

    private void updateConfig(List<T> newValue) {
        if (newValue == null) {
            log.warn("sentinel config is null, no be update");
            return;
        }
        getProperty().updateValue(newValue);
    }
}
