package com.wind.context.variable.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 注入当前上下文中的登录用户名，必填
 *
 * @author wuxp
 * @date 2023-10-24 20:55
 **/
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@ContextVariable(name = ContextVariableNames.USER_NAME)
public @interface ContextUserNme {

    /**
     * {@link ContextVariable#override()}
     */
    boolean override() default true;

    /**
     * {@link ContextVariable#required()}
     */
    boolean required() default true;
}
