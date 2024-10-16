package com.wind.core;


import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;

/**
 * 可写的上下文变量
 *
 * @author wuxp
 * @date 2024-07-04 13:24
 **/
public interface WritableContextVariables extends ReadonlyContextVariables {

    /**
     * 添加变量
     *
     * @param name 变量名
     * @param val  变量值
     * @return this
     */
    WritableContextVariables putVariable(@NotBlank String name, @Nullable Object val);
}
