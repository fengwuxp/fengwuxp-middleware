package com.wind.sensitive;

import com.wind.common.WindConstants;

import java.util.Collection;
import java.util.Collections;

/**
 * 对象 {@param T} 脱敏器
 *
 * @author wuxp
 */
public interface ObjectSanitizer<T, R> {

    /**
     * 将对象强制以星号的方式打印
     */
    ObjectSanitizer<Object, String> ASTERISK = (value, keys) -> value == null ? WindConstants.NULL : "******";

    /**
     * 将一个 java 对象脱敏
     *
     * @param value 需要脱敏的值
     * @return 脱敏后的字符串
     */
    default R sanitize(T value) {
        return sanitize(value, Collections.emptyList());
    }

    /**
     * @param value 需要脱敏的值
     * @param keys  需要脱敏的 keys
     * @return 脱敏后的字符串
     */
    R sanitize(T value, Collection<String> keys);
}