package com.wind.common.util;

import com.wind.common.exception.AssertUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 反射工具类
 *
 * @author wuxp
 * @date 2024-08-02 14:57
 **/
public final class WindReflectUtils {

    private static final Field[] EMPTY = new Field[0];

    /**
     * 根据注解查找 {@link Field}，会递归查找超类
     *
     * @param clazz           类类型
     * @param annotationClass 注解类型
     * @return 字段列表
     */
    public static Field[] findFields(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        AssertUtils.notNull(clazz, "argument clazz  must not null");
        AssertUtils.notNull(annotationClass, "argument annotationClass  must not null");
        return getClazzFields(clazz)
                .stream()
                .filter(field -> field.isAnnotationPresent(annotationClass))
                .toArray(Field[]::new);
    }

    /**
     * 根据字段名称查找 {@link Field}，会递归查找超类
     *
     * @param clazz      类类型
     * @param fieldNames 注解类型
     * @return 字段列表
     */
    public static Field[] findFields(Class<?> clazz, Collection<String> fieldNames) {
        AssertUtils.notNull(clazz, "argument clazz  must not null");
        AssertUtils.notEmpty(fieldNames, "argument fieldNames  must not empty");
        Set<String> names = new HashSet<>(fieldNames);
        return getClazzFields(clazz)
                .stream()
                .filter(field -> names.contains(field.getName()))
                .distinct()
                .toArray(Field[]::new);
    }

    /**
     * 获取类的所有字段名称
     *
     * @param clazz 类类型
     * @return 字段名称列表
     */
    public static List<String> getFieldNames(Class<?> clazz) {
        AssertUtils.notNull(clazz, "argument clazz  must not null");
        return getClazzFields(clazz)
                .stream()
                .map(Field::getName)
                .collect(Collectors.toList());
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
