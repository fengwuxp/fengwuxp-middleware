package com.wind.server.configcenter;

import com.wind.common.WindConstants;
import com.wind.common.enums.ConfigFileType;
import com.wind.common.enums.WindMiddlewareType;
import com.wind.common.util.ClassDetectionUtils;
import com.wind.configcenter.core.ConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * dynamic-tp 配置 探测器
 *
 * @author wuxp
 * @date 2024-06-25 15:05
 **/
@Slf4j
public final class WindDynamicTpConfigDescriptorDetector {

    /**
     * web server type
     */
    private static final String webServerType;

    /**
     * mq type
     */
    private static final String mqType;

    /**
     * 告警平台配置
     */
    private static final String alertPlatform;

    static {
        webServerType = getWebServerType();
        mqType = getMqType();
        alertPlatform = getPlatform();
    }

    private WindDynamicTpConfigDescriptorDetector() {
        throw new AssertionError();
    }

    /**
     * 获取 dynamic-tp 需要加载的配置文件
     *
     * @param appName 应用名称
     * @return 配置文件列表
     */
    public static List<ConfigRepository.ConfigDescriptor> getConfigDescriptors(String appName) {
        return Stream.of(webServerType, mqType, alertPlatform)
                .filter(StringUtils::hasText)
                .map(name -> {
                    SimpleConfigDescriptor result = SimpleConfigDescriptor.of(String.format("%s-dynamictp-%s", appName, name), WindMiddlewareType.DYNAMIC_TP.name(), ConfigFileType.YAML);
                    // 支持动态监听
                    result.setRefreshable(true);
                    return result;
                })
                .collect(Collectors.toList());
    }

    private static String getWebServerType() {
        // TODO 待完善
        boolean isDynamicWebServer = ClassDetectionUtils.isPresent("org.dromara.dynamictp.starter.adapter.webserver.AbstractWebServerDtpAdapter");
        if (!isDynamicWebServer) {
            return WindConstants.EMPTY;
        }
        if (ClassDetectionUtils.isPresent("org.apache.catalina.Container")) {
            return "tomcat";
        }
        if (ClassDetectionUtils.isPresent("io.undertow.Undertow")) {
            return "undertow";
        }
        return WindConstants.EMPTY;
    }

    private static String getMqType() {
        // TODO 待完善
        if (ClassDetectionUtils.isPresent("org.dromara.dynamictp.adapter.rocketmq.RocketMqDtpAdapter")) {
            return ClassDetectionUtils.isPresent("org.apache.rocketmq.client.MQAdmin") ? "rocketmq" : WindConstants.EMPTY;
        }
        return WindConstants.EMPTY;
    }

    private static String getPlatform() {
        if (ClassDetectionUtils.isPresent("org.dromara.dynamictp.starter.cloud.nacos.autoconfigure.DtpCloudNacosAutoConfiguration")) {
            return ClassDetectionUtils.isPresent("com.wind.nacos.NacosConfigRepository") ? "alert" : WindConstants.EMPTY;
        }
        return WindConstants.EMPTY;
    }

}
