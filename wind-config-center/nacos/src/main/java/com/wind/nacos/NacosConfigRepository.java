package com.wind.nacos;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.wind.common.exception.BaseException;
import com.wind.common.exception.DefaultExceptionCode;
import com.wind.configcenter.core.ConfigRepository;
import com.wind.nacos.client.NacosPropertySource;
import com.wind.nacos.parser.NacosDataParserHandler;
import lombok.AllArgsConstructor;
import org.springframework.core.env.PropertySource;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * 从 nacos 中加载配置
 * @author wuxp
 * @date 2023-10-15 14:59
 **/
@AllArgsConstructor
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
        try {
            List<PropertySource<?>> result = NacosDataParserHandler.getInstance().parseNacosData(descriptor.getConfigId(), config, descriptor.getFileType().getFileExtension());
            collectNacosPropertySource(result, descriptor);
            return result;
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
