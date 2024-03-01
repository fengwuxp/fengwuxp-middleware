package com.wind.server.configuration;

import com.wind.common.exception.AssertUtils;
import com.wind.context.injection.MethodParameterInjector;
import com.wind.script.auditlog.AuditLogRecorder;
import com.wind.script.auditlog.ScriptAuditLogRecorder;
import com.wind.server.actuate.health.GracefulShutdownHealthIndicator;
import com.wind.server.aop.WindControllerMethodInterceptor;
import com.wind.server.initialization.WindApplicationStartedListener;
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
import org.springframework.core.Ordered;

import java.util.Collection;
import java.util.Collections;

import static com.wind.common.WindConstants.CONTROLLER_METHOD_ASPECT_NAME;
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
    @ConditionalOnProperty(prefix = CONTROLLER_METHOD_ASPECT_NAME, name = ENABLED_NAME, havingValue = TRUE, matchIfMissing = true)
    public WindControllerMethodInterceptor windControllerMethodInterceptor(ApplicationContext context) {
        ScriptAuditLogRecorder recorder = null;
        Collection<MethodParameterInjector> injectors = Collections.emptyList();
        try {
            recorder = context.getBean(ScriptAuditLogRecorder.class);
            injectors = context.getBeansOfType(MethodParameterInjector.class).values();
        } catch (BeansException exception) {
            log.error("un enable audit log or method parameter", exception);
        }
        return new WindControllerMethodInterceptor(recorder, MethodParameterInjector.composite(injectors));
    }

    @Bean
    @ConditionalOnBean(WindControllerMethodInterceptor.class)
    public DefaultBeanFactoryPointcutAdvisor windControllerMethodAspectPointcutAdvisor(WindControllerMethodInterceptor advice, WindServerProperties properties) {
        String expression = properties.getControllerMethodAspect().getExpression();
        AssertUtils.hasLength(expression, String.format("%s 未配置", CONTROLLER_METHOD_ASPECT_NAME));
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(expression);
        DefaultBeanFactoryPointcutAdvisor advisor = new DefaultBeanFactoryPointcutAdvisor();
        // 拦截优先级设置为最高
        advisor.setOrder(Ordered.HIGHEST_PRECEDENCE);
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
    public WindApplicationStartedListener windApplicationStartedListener() {
        return new WindApplicationStartedListener();
    }

}
