package com.wind.sensitive.sanitizer;

import com.wind.common.exception.BaseException;
import com.wind.common.exception.DefaultExceptionCode;
import com.wind.sensitive.ObjectSanitizer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wuxp
 * @date 2024-08-02 15:12
 **/
public final class SanitizerFactory {

    @SuppressWarnings("rawtypes")
    private static final Map<Class<? extends ObjectSanitizer>, ObjectSanitizer<Object, Object>> SANITIZERS = new ConcurrentHashMap<>();

    private SanitizerFactory() {
        throw new AssertionError();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static ObjectSanitizer<Object, Object> getObjectSanitizer(Class<? extends ObjectSanitizer> sanitizerClass) {
        return SANITIZERS.computeIfAbsent(sanitizerClass, key -> {
            try {
                return sanitizerClass.newInstance();
            } catch (InstantiationException | IllegalAccessException exception) {
                throw new BaseException(DefaultExceptionCode.COMMON_ERROR, "New ObjectSanitizer error", exception);
            }
        });
    }
}
