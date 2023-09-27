package com.wind.server.configuration;

import com.wind.common.exception.AssertUtils;
import com.wind.script.auditlog.AuditLogRecorder;
import com.wind.script.auditlog.ScriptAuditLogBuilder;
import com.wind.server.logging.ControllerLogAspect;
import com.wind.server.logging.WebAuditLogBuilder;
import com.wind.server.web.exception.RespfulErrorAttributes;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultBeanFactoryPointcutAdvisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.wind.common.WindConstants.CONTROLLER_ASPECT_LOG_EXPRESSION;
import static com.wind.common.WindConstants.WIND_SERVER_PROPERTIES_PREFIX;

/**
 * @author wuxp
 * @date 2023-09-26 15:53
 **/
@Configuration
@ConditionalOnProperty(prefix = WIND_SERVER_PROPERTIES_PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class WindServerAutoConfiguration {

    @Bean
    public RespfulErrorAttributes respfulErrorAttributes(){
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
    @ConditionalOnProperty(prefix = WIND_SERVER_PROPERTIES_PREFIX + ".controller-aspect-log", name = "expression")
    public DefaultBeanFactoryPointcutAdvisor controllerLogAspectPointcutAdvisor(ControllerLogAspect apiInterceptor,
                                                                 @Value("${" + CONTROLLER_ASPECT_LOG_EXPRESSION + "}") String aspectjExpression) {
        AssertUtils.hasLength(aspectjExpression, String.format("%s 未配置", CONTROLLER_ASPECT_LOG_EXPRESSION));
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(aspectjExpression);
        DefaultBeanFactoryPointcutAdvisor advisor = new DefaultBeanFactoryPointcutAdvisor();
        advisor.setPointcut(pointcut);
        advisor.setAdvice(apiInterceptor);
        return advisor;
    }


}
