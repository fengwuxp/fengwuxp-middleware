package com.wind.server.configuration;

import com.wind.common.exception.AssertUtils;
import com.wind.context.injection.MethodParameterInjector;
import com.wind.script.auditlog.AuditLogRecorder;
import com.wind.script.auditlog.ScriptAuditLogRecorder;
import com.wind.server.actuate.health.GracefulShutdownHealthIndicator;
import com.wind.server.aop.WindControllerMethodAspect;
import com.wind.server.initialization.SystemInitializationListener;
import com.wind.server.initialization.SystemInitializer;
import com.wind.server.logging.WebAuditLogRecorder;
import com.wind.server.web.exception.RespfulErrorAttributes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultBeanFactoryPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.Map;

import static com.wind.common.WindConstants.CONTROLLER_METHOD_LOG_NAME;
import static com.wind.common.WindConstants.ENABLED_NAME;
import static com.wind.common.WindConstants.TRUE;
import static com.wind.common.WindConstants.WIND_SERVER_PROPERTIES_PREFIX;

/**
 * @author wuxp
 * @date 2023-09-26 15:53
 **/
@Configuration
@EnableConfigurationProperties(value = {WindServerProperties.class})
@ConditionalOnProperty(prefix = WIND_SERVER_PROPERTIES_PREFIX, name = ENABLED_NAME, havingValue = TRUE, matchIfMissing = true)
@Slf4j
public class WindServerAutoConfiguration {

    @Bean
    public RespfulErrorAttributes respfulErrorAttributes() {
        return new RespfulErrorAttributes(new DefaultErrorAttributes());
    }

    @Bean
    @ConditionalOnBean(AuditLogRecorder.class)
    @ConditionalOnMissingBean(ScriptAuditLogRecorder.class)
    public WebAuditLogRecorder webAuditLogRecorder(AuditLogRecorder recorder) {
        return new WebAuditLogRecorder(recorder);
    }

    @Bean
    @ConditionalOnProperty(prefix = CONTROLLER_METHOD_LOG_NAME, name = ENABLED_NAME, havingValue = TRUE, matchIfMissing = true)
    public WindControllerMethodAspect windControllerMethodAspect(ApplicationContext context) {
        ScriptAuditLogRecorder recorder = null;
        try {
            recorder = context.getBean(ScriptAuditLogRecorder.class);
        } catch (BeansException exception) {
            log.debug("un enable audit log");
        }
        Map<String, MethodParameterInjector> injectors = context.getBeansOfType(MethodParameterInjector.class);
        return new WindControllerMethodAspect(recorder, MethodParameterInjector.composite(injectors.values()));
    }

    @Bean
    @ConditionalOnProperty(prefix = CONTROLLER_METHOD_LOG_NAME, name = "expression")
    public DefaultBeanFactoryPointcutAdvisor windControllerMethodAspectPointcutAdvisor(WindControllerMethodAspect advice, WindServerProperties properties) {
        String expression = properties.getControllerAspect().getExpression();
        AssertUtils.hasLength(expression, String.format("%s 未配置", CONTROLLER_METHOD_LOG_NAME));
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(expression);
        DefaultBeanFactoryPointcutAdvisor advisor = new DefaultBeanFactoryPointcutAdvisor();
        advisor.setPointcut(pointcut);
        advisor.setAdvice(advice);
        return advisor;
    }

    @Bean
    @ConditionalOnProperty(prefix = WIND_SERVER_PROPERTIES_PREFIX + ".health.graceful-shutdown", name = "enabled", havingValue = "true", matchIfMissing = true)
    public GracefulShutdownHealthIndicator gracefulShutdownHealthIndicator() {
        return new GracefulShutdownHealthIndicator();
    }

    @Bean
    @ConditionalOnBean(SystemInitializer.class)
    public SystemInitializationListener systemInitializationListener(Collection<SystemInitializer> initializers) {
        return new SystemInitializationListener(initializers);
    }

}
