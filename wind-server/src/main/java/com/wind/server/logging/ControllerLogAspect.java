package com.wind.server.logging;

import com.wind.common.WindConstants;
import com.wind.script.auditlog.ScriptAuditLogBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;

/**
 * 打印控制器日志
 *
 * @author wuxp
 */
@Slf4j
@AllArgsConstructor
public final class ControllerLogAspect implements MethodInterceptor {

    private final ScriptAuditLogBuilder auditLogBuilder;

    @Override
    public Object invoke(@Nonnull MethodInvocation invocation) throws Throwable {
        if (log.isDebugEnabled()) {
            log.debug("请求方法：{}，参数：{}", getRequestMethodDesc(invocation.getMethod()), invocation.getArguments());
        }
        try {
            Object result = invocation.proceed();
            if (log.isDebugEnabled()) {
                log.debug("请求方法：{}，响应：{}", getRequestMethodDesc(invocation.getMethod()), result);
            }
            recordOperationLog(invocation, result, null);
            return result;
        } catch (Throwable throwable) {
            log.error("请求方法：{} 异常， 参数：{}，message：{}", invocation.getMethod(), invocation.getArguments(), throwable.getMessage(), throwable);
            recordOperationLog(invocation, null, throwable);
            throw throwable;
        }
    }

    private String getRequestMethodDesc(Method method) {
        return String.format("%s%s%s", method.getDeclaringClass().getName(), WindConstants.SHARP, method.getName());
    }

    private void recordOperationLog(MethodInvocation invocation, Object result, Throwable throwable) {
        if (auditLogBuilder == null) {
            return;
        }
        auditLogBuilder.recordLog(invocation.getArguments(), result, invocation.getMethod(), throwable);
    }

}
