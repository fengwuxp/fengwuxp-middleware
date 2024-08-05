package com.wind.sensitive;

import com.wind.common.exception.BaseException;
import com.wind.common.exception.DefaultExceptionCode;
import com.wind.common.util.WindReflectUtils;
import com.wind.sensitive.annotation.Sensitive;
import com.wind.sensitive.sanitizer.SanitizerFactory;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * 默认对象脱敏器
 *
 * @author wuxp
 * @date 2024-08-02 14:33
 **/
public class DefaultObjectSanitizer implements ObjectSanitizer<Object, Object> {

    private final Map<Class<?>, Field[]> sensitiveFields = new ConcurrentReferenceHashMap<>();

    @Override
    public Object sanitize(Object value, Collection<String> keys) {
        if (value == null) {
            return null;
        }
        Class<?> clazz = value.getClass();
        if (clazz.isAnnotationPresent(Sensitive.class)) {
            Field[] fields = getSensitiveFields(clazz);
            for (Field field : fields) {
                try {
                    sanitizeField(field, value);
                } catch (Exception exception) {
                    throw new BaseException(DefaultExceptionCode.COMMON_ERROR, "object sanitize error", exception);
                }
            }
        }
        return value;
    }

    /**
     * 是否需要脱敏
     *
     * @param clazz 类类型
     * @return true 需要
     */
    public boolean requiredSanitize(Class<?> clazz) {
        return clazz.isAnnotationPresent(Sensitive.class) && getSensitiveFields(clazz).length > 0;
    }

    private Field[] getSensitiveFields(Class<?> clazz) {
        return sensitiveFields.computeIfAbsent(clazz, k -> WindReflectUtils.findFields(k, Sensitive.class));
    }

    private void sanitizeField(Field field, Object val) throws Exception {
        Object o = field.get(val);
        if (o == null) {
            return;
        }
        Sensitive annotation = field.getAnnotation(Sensitive.class);
        Object sanitize = SanitizerFactory.getObjectSanitizer(annotation.sanitizer()).sanitize(o, Arrays.asList(annotation.names()));
        field.set(val, sanitize);
    }


}
