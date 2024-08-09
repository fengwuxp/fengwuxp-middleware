package com.wind.mask;

import java.util.Collection;
import java.util.Collections;

/**
 * 对象 {@param T} 脱敏器
 *
 * @author wuxp
 */
public interface ObjectMasker<T, R> extends WindMasker<T, R> {

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