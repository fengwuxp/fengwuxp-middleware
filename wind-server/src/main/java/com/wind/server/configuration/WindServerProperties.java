package com.wind.server.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.wind.common.WindConstants.WIND_SERVER_PROPERTIES_PREFIX;

/**
 * @author wuxp
 * @date 2023-09-27 11:56
 **/
@Data
@ConfigurationProperties(prefix = WIND_SERVER_PROPERTIES_PREFIX)
public class WindServerProperties {

    /**
     * 开启 wind server supports
     */
    private boolean enabled = true;

    /**
     * 控制器日志 Aop 拦截配置
     */
    private ControllerLogAspectProperties controllerLogAspect;

    @Data
    public static class ControllerLogAspectProperties {

        /**
         * spring aop aspect pointcut 表达式
         * <a href="https://zhuanlan.zhihu.com/p/63001123">spring aop中pointcut表达式完整版</a>
         * <a href="https://docs.spring.io/spring-framework/reference/core/aop/ataspectj/pointcuts.html">Expression Supported Pointcut</a>
         */
        private String expression;
    }
}
