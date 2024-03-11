package com.wind.sensitive;

/**
 * 对象 {@param T} toString 脱敏器
 *
 * @author wuxp
 */
public interface ObjectSanitizer<T> {

    String NULL_TEXT = "null";

    /**
     * 将对象强制以星号的方式打印
     */
    ObjectSanitizer<Object> ASTERISK = value -> value == null ? NULL_TEXT : "******";

    /**
     * 将一个 java 对象转换为字符串，并做脱敏
     *
     * @param value 需要脱敏的值
     * @return 脱敏后的字符串
     */
    String sanitize(T value);
}