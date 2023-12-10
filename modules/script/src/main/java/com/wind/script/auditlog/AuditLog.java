package com.wind.script.auditlog;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 用于记录操作日志
 *
 * @author wxup
 */
@Target({METHOD})
@Retention(RUNTIME)
public @interface AuditLog {

    /**
     * 上下文默认提供的
     * args：请求参数列表
     * result：方法返回结果
     * 请求上下文中的变量
     *
     * @return 日志模板字符串，支持 spring expression
     */
    String value() default "";

    /**
     * @return 审计操作业务功能分组，比如：用户模块、订单模块
     */
    String group() default "default";

    /**
     * @return 审计操作动作，比如：CREATE
     */
    String operation() default "";

    /**
     * @return 审计操作资源类型，可以是操作对象的类名 OR 表名
     */
    String resourceType() default "";

    /**
     * 支持 spring expression
     *
     * @return 审计操作目标资源标识
     */
    String resourceId() default "";

    /**
     * 支持从表达式中获取
     *
     * @return 审计操作备注
     */
    String remark() default "";

}
