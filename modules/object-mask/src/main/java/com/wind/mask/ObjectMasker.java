package com.wind.mask;

import com.wind.common.WindConstants;

import java.util.Collection;
import java.util.Collections;

/**
 * 对象 {@param T} 脱敏器
 *
 * @author wuxp
 */
public interface ObjectMasker<T, R> {

    /**
     * 将对象强制以星号的方式打印
     */
    ObjectMasker<Object, String> ASTERISK = (value, keys) -> value == null ? WindConstants.NULL : "******";

    /**
     * 将一个 java 对象脱敏
     *
     * @param value 需要脱敏的值
     * @return 脱敏后的字符串
     */
    default R mask(T value) {
        return mask(value, Collections.emptyList());
    }

    /**
     * @param value 需要脱敏的值
     * @param keys  需要脱敏的 keys
     * @return 脱敏后的字符串
     */
    R mask(T value, Collection<String> keys);
}