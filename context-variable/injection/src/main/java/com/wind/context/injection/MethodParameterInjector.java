package com.wind.context.injection;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * 方法参数注入器
 *
 * @author wuxp
 * @date 2023-10-25 08:58
 **/
public interface MethodParameterInjector {

    /**
     * 注入方法参数中被注解标记的的字段或参数
     *
     * @param method    方法
     * @param arguments 方法参数
     */
    void inject(Method method, Object[] arguments);


    /**
     * 合并一组方法参数注入器
     *
     * @param delegates 委托注入器集合
     * @return 合并后的参数注入器
     */
    static MethodParameterInjector composite(Collection<MethodParameterInjector> delegates) {
        return (method, arguments) -> delegates.forEach(delegate -> delegate.inject(method, arguments));
    }
}
