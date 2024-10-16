package com.wind.core;

import com.wind.common.exception.AssertUtils;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import java.util.Map;

/**
 * readonly context variables
 *
 * @author wuxp
 * @date 2024-06-13 09:12
 **/
public interface ReadonlyContextVariables {

    /**
     * 获取上下文变量
     *
     * @param name 变量名称
     * @return 上下文变量
     */
    @Nullable
    @SuppressWarnings("unchecked")
    default <T> T getContextVariable(@NotBlank String name) {
        AssertUtils.hasText(name, "argument context variable name must not empty");
        Map<String, Object> contextVariables = getContextVariables();
        return contextVariables == null ? null : (T) contextVariables.get(name);
    }

    /**
     * 获取上下文变量，如果 name 对应的变量不存在则抛异常
     *
     * @param name 变量名称
     * @return 上下文变量
     */
    default <T> T requireContextVariable(@NotBlank String name) {
        T result = getContextVariable(name);
        AssertUtils.notNull(result, () -> String.format("context variable %s must not null", name));
        return result;
    }

    /**
     * @return 获取所有的只读上下文变量
     */
    Map<String, Object> getContextVariables();
}
