package com.wind.server.configuration;

import com.wind.common.exception.AssertUtils;
import com.wind.script.auditlog.AuditLogRecorder;
import com.wind.script.auditlog.ScriptAuditLogBuilder;
import com.wind.server.actuate.health.GracefulShutdownHealthIndicator;
import com.wind.server.initialization.SystemInitializationListener;
import com.wind.server.initialization.SystemInitializer;
import com.wind.server.logging.ControllerLogAspect;
import com.wind.server.logging.WebAuditLogBuilder;
import com.wind.server.web.exception.RespfulErrorAttributes;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultBeanFactoryPointcutAdvisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;

import static com.wind.common.WindConstants.CONTROLLER_ASPECT_LOG_NAME;
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
public class WindServerAutoConfiguration {

    @Bean
    public RespfulErrorAttributes respfulErrorAttributes() {
        return new RespfulErrorAttributes(new DefaultErrorAttributes());
    }

    @Bean
    @ConditionalOnBean(AuditLogRecorder.class)
    @ConditionalOnMissingBean(ScriptAuditLogBuilder.class)
    public WebAuditLogBuilder webAuditLogBuilder(AuditLogRecorder recorder) {
        return new WebAuditLogBuilder(recorder);
    }

    @Bean
    @ConditionalOnBean(ScriptAuditLogBuilder.class)
    public ControllerLogAspect controllerLogAspect(ScriptAuditLogBuilder auditLogBuilder) {
        return new ControllerLogAspect(auditLogBuilder);
    }

    @Bean
    @ConditionalOnBean(value = {ControllerLogAspect.class})
    @ConditionalOnProperty(prefix = CONTROLLER_ASPECT_LOG_NAME, name = "expression")
    public DefaultBeanFactoryPointcutAdvisor controllerLogAspectPointcutAdvisor(ControllerLogAspect apiInterceptor, WindServerProperties properties) {
        String expression = properties.getControllerLogAspect().getExpression();
        AssertUtils.hasLength(expression, String.format("%s 未配置", CONTROLLER_ASPECT_LOG_NAME));
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(expression);
        DefaultBeanFactoryPointcutAdvisor advisor = new DefaultBeanFactoryPointcutAdvisor();
        advisor.setPointcut(pointcut);
        advisor.setAdvice(apiInterceptor);
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
