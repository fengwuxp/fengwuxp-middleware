package com.wind.server.logging;

import com.wind.common.WindConstants;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

import static com.wind.common.WindConstants.WIND_SERVER_PROPERTIES_PREFIX;

/**
 * 打印控制器日志
 *
 * @author wuxp
 */
@Slf4j
@Aspect
@Component
@ConditionalOnProperty(prefix = WIND_SERVER_PROPERTIES_PREFIX, value = "enable-controller-log", havingValue = "true")
public final class ControllerLogAspect {

    /**
     * 定义切入点 就是需要拦截的切面
     */
    @Pointcut("execution(public * com.*.*.controller.*.*(..))")
    public void controllerMethod() {
    }


    @Around("controllerMethod()")
    public Object log(ProceedingJoinPoint point) throws Throwable {
        if (log.isDebugEnabled()) {
            log.debug("请求方法：{}，参数：{}", getRequestMethodDesc(point.getSignature()), point.getArgs());
        }
        try {
            Object result = point.proceed();
            if (log.isDebugEnabled()) {
                log.debug("请求方法：{}，响应：{}", getRequestMethodDesc(point.getSignature()), result);
            }
            return result;
        } catch (Throwable throwable) {
            log.error("请求方法：{} 异常， 参数：{}，message：{}", getRequestMethodDesc(point.getSignature()), point.getArgs(), throwable.getMessage(), throwable);
            throw throwable;
        }
    }

    private String getRequestMethodDesc(Signature signature) {
        Method method = ((MethodSignature) signature).getMethod();
        return String.format("%s%s%s", method.getDeclaringClass().getName(), WindConstants.HASHTAG, method.getName());
    }
}
