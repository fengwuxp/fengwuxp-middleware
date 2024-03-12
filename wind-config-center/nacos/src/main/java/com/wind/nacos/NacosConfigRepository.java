package com.wind.nacos;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.alibaba.nacos.api.exception.NacosException;
import com.wind.common.exception.BaseException;
import com.wind.common.exception.DefaultExceptionCode;
import com.wind.configcenter.core.ConfigRepository;
import com.wind.nacos.client.NacosPropertySource;
import com.wind.nacos.parser.NacosDataParserHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.PropertySource;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * 从 nacos 中加载配置
 *
 * @author wuxp
 * @date 2023-10-15 14:59
 **/
@AllArgsConstructor
@Slf4j
public class NacosConfigRepository implements ConfigRepository {

    private final ConfigService configService;

    private final NacosConfigProperties properties;

    @Override
    public String getTextConfig(ConfigDescriptor descriptor) {
        try {
            return configService.getConfig(descriptor.getConfigId(), descriptor.getGroup(), properties.getTimeout());
        } catch (NacosException exception) {
            throw new BaseException(DefaultExceptionCode.COMMON_ERROR, String.format("load config：%s failure", descriptor.getConfigId()), exception);
        }
    }

    @Override
    public List<PropertySource<?>> getConfigs(ConfigDescriptor descriptor) {
        String config = getTextConfig(descriptor);
        List<PropertySource<?>> result = getPropertySources(descriptor, config);
        collectNacosPropertySource(result, descriptor);
        return result;
    }

    @Override
    public ConfigSubscription onChange(ConfigDescriptor descriptor, ConfigListener listener) {
        if (descriptor.isRefreshable()) {
            log.warn("config unsupported refresh, dataId = {}，group = {}", descriptor.getConfigId(), descriptor.getGroup());
            return ConfigSubscription.empty(descriptor);

        }
        final AbstractListener wrapperListener = new AbstractListener() {
            @Override
            public void receiveConfigInfo(String content) {
                listener.change(content);
                listener.change(getPropertySources(descriptor, content));
            }
        };
        try {
            configService.addListener(descriptor.getConfigId(), descriptor.getGroup(), wrapperListener);
        } catch (NacosException exception) {
            throw new BaseException(DefaultExceptionCode.COMMON_ERROR, exception.getMessage(), exception);
        }
        return new ConfigSubscription() {
            @Override
            public ConfigDescriptor getConfigDescriptor() {
                return descriptor;
            }

            @Override
            public void unsubscribe() {
                // 移除订阅
                configService.removeListener(descriptor.getConfigId(), descriptor.getGroup(), wrapperListener);
            }
        };
    }

    private List<PropertySource<?>> getPropertySources(ConfigDescriptor descriptor, String content) {
        try {
            return NacosDataParserHandler.getInstance().parseNacosData(descriptor.getConfigId(), content, descriptor.getFileType().getFileExtension());
        } catch (IOException exception) {
            throw new BaseException(DefaultExceptionCode.COMMON_ERROR, String.format("parse config：%s failure", descriptor.getConfigId()), exception);
        }
    }

    private void collectNacosPropertySource(List<PropertySource<?>> propertySources, ConfigDescriptor descriptor) {
        NacosPropertySource nacosPropertySource = new NacosPropertySource(propertySources,
                descriptor.getGroup(), descriptor.getConfigId(), new Date(), descriptor.isRefreshable());
        NacosPropertySourceRepository.collectNacosPropertySource(nacosPropertySource);
    }
}
