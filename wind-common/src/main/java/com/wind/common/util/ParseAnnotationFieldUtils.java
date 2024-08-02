package com.wind.common.util;

import com.wind.common.exception.AssertUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author wuxp
 * @date 2024-08-02 14:57
 **/
public final class ParseAnnotationFieldUtils {

    private static final Field[] EMPTY = new Field[0];

    public static Field[] parse(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        AssertUtils.notNull(clazz, "argument clazz  must not null");
        AssertUtils.notNull(annotationClass, "argument annotationClass  must not null");
        List<Field> clazzFields = getClazzFields(clazz);
        Field[] result = clazzFields
                .stream()
                .filter(field -> field.isAnnotationPresent(annotationClass))
                .toArray(Field[]::new);
        Field.setAccessible(result, true);
        return result;
    }

    private static List<Field> getClazzFields(Class<?> clazz) {
        if (clazz == Object.class) {
            return Collections.emptyList();
        }
        List<Field> result = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
        result.addAll(getClazzFields(clazz.getSuperclass()));
        return result;
    }
}
