package com.wind.sensitive.annotation;

import com.wind.sensitive.ObjectSanitizer;
import com.wind.sensitive.SensitiveRuleGroup;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标记某个字段（参数）为敏感的，需要做脱敏处理
 *
 * @author wuxp
 */
@Inherited
@Documented
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Sensitive {

    /**
     * 仅在配置在类上是需要，用于匹配字段名称，支持正则表达式
     *
     * @return 字段名称列表
     */
    String[] names() default {};

    /**
     * 使用的全局脱敏 {@link ObjectSanitizer}类型
     * 如果想针对字段使用自定义的 ObjectSanitizer 实例，参见{@link SensitiveRuleGroup.GroupBuilder}
     *
     * @return ObjectSanitizer 类类型
     */
    @SuppressWarnings("rawtypes")
    Class<? extends ObjectSanitizer> sanitizer();
}