package com.wind.mask;

import com.wind.common.WindConstants;

/**
 * 脱敏器
 *
 * @author wuxp
 * @date 2024-08-08 09:18
 **/
public interface WindMasker<T, R> {

    /**
     * 将对象强制以星号的方式打印
     */
    WindMasker<Object, String> ASTERISK = v -> v == null ? WindConstants.NULL : "******";

    WindMasker<Object, Object> NONE = v -> v;

    /**
     * 将一个 java 对象脱敏
     *
     * @param value 需要脱敏的值
     * @return 脱敏后的字符串
     */
    R mask(T value);
}
