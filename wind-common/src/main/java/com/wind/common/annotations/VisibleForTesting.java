package com.wind.common.annotations;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotates a program element that exists, or is more widely visible than otherwise necessary, only
 * for use in test code.
 * <p>
 * 用于标记代码为了测试而改变可见性
 *
 * @author wuxp
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
public @interface VisibleForTesting {
}
