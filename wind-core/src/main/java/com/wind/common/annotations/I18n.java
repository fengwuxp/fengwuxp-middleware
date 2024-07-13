package com.wind.common.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标记某个类的字段需要国际化
 * 注意：仅字符串类型的字段才能国际化
 *
 * @author wuxp
 * @date 2024-07-11 15:31
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface I18n {

    String OBJECT_VARIABLE_NAME = "that";

    /**
     * 如果 name 为空，那么将使用该注解标记的字段的值作为 key 进行查找
     * 支持 spring expression，例如：XXX_#that.id
     * 上下文变量：
     * that：表示当前类对象实例
     *
     * @return 用于查找国际化消息的 key name
     */
    String name() default "";
}
