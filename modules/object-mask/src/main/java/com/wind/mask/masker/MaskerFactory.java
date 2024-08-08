package com.wind.mask.masker;

import com.wind.common.exception.BaseException;
import com.wind.common.exception.DefaultExceptionCode;
import com.wind.mask.WindMasker;

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
    private static final Map<Class<? extends WindMasker>, WindMasker<Object, Object>> MASKERS = new ConcurrentHashMap<>();

    private MaskerFactory() {
        throw new AssertionError();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static WindMasker<Object, Object> getMasker(Class<? extends WindMasker> maskerType) {
        return MASKERS.computeIfAbsent(maskerType, key -> {
            if (maskerType==WindMasker.class){
                return WindMasker.NONE;
            }
            try {
                return maskerType.newInstance();
            } catch (InstantiationException | IllegalAccessException exception) {
                throw new BaseException(DefaultExceptionCode.COMMON_ERROR, "New WindMasker error", exception);
            }
        });
    }
}
