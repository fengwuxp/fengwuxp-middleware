package com.wind.mask.masker;

import com.wind.common.exception.BaseException;
import com.wind.common.exception.DefaultExceptionCode;
import com.wind.mask.ObjectMasker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 脱敏器工厂
 *
 * @author wuxp
 * @date 2024-08-02 15:12
 **/
public final class MaskerFactory {

    @SuppressWarnings("rawtypes")
    private static final Map<Class<? extends ObjectMasker>, ObjectMasker<Object, Object>> MASKERS = new ConcurrentHashMap<>();

    private MaskerFactory() {
        throw new AssertionError();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static ObjectMasker<Object, Object> getObjectSanitizer(Class<? extends ObjectMasker> sanitizerClass) {
        return MASKERS.computeIfAbsent(sanitizerClass, key -> {
            try {
                return sanitizerClass.newInstance();
            } catch (InstantiationException | IllegalAccessException exception) {
                throw new BaseException(DefaultExceptionCode.COMMON_ERROR, "New ObjectSanitizer error", exception);
            }
        });
    }
}
