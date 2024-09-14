package com.wind.common.util;

import com.wind.common.exception.AssertUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.ResolvableType;
import org.springframework.util.ConcurrentReferenceHashMap;

import javax.validation.constraints.NotNull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 反射工具类，注意不支持静态字段
 *
 * @author wuxp
 * @date 2024-08-02 14:57
 **/
public final class WindReflectUtils {

    private static final Map<Class<?>, List<Field>> FIELDS = new ConcurrentReferenceHashMap<>();

    /**
     * 根据注解查找 {@link Field}，会递归查找超类
     *
     * @param clazz           类类型
     * @param annotationClass 注解类型
     * @return 字段列表
     */
    @NotNull
    public static Field[] findFields(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        AssertUtils.notNull(clazz, "argument clazz  must not null");
        AssertUtils.notNull(annotationClass, "argument annotationClass  must not null");
        Field[] result = getMemberFields(clazz)
                .stream()
                .filter(field -> field.isAnnotationPresent(annotationClass))
                .toArray(Field[]::new);
        Field.setAccessible(result, true);
        return result;
    }

    /**
     * 根据字段名称查找 {@link Field}，会递归查找超类
     *
     * @param clazz      类类型
     * @param fieldNames 字段名称集合
     * @return 字段列表
     */
    @NotNull
    public static Field[] findFields(Class<?> clazz, Collection<String> fieldNames) {
        AssertUtils.notNull(clazz, "argument clazz  must not null");
        AssertUtils.notEmpty(fieldNames, "argument fieldNames  must not empty");
        Set<String> names = new HashSet<>(fieldNames);
        Field[] result = getMemberFields(clazz)
                .stream()
                .filter(field -> names.contains(field.getName()))
                .distinct()
                .toArray(Field[]::new);
        Field.setAccessible(result, true);
        return result;
    }

    /**
     * 根据字段名称查找 {@link Field}，会递归查找超类
     *
     * @param clazz     类类型
     * @param fieldName 字段名称
     * @return 字段
     */
    @NotNull
    public static Field findField(Class<?> clazz, String fieldName) {
        Field[] fields = findFields(clazz, Collections.singleton(fieldName));
        AssertUtils.notEmpty(fields, String.format("not found name = %s field", fieldName));
        return fields[0];
    }

    @NotNull
    public static Field[] getFields(Class<?> clazz) {
        return findFields(clazz, getFieldNames(clazz));
    }

    /**
     * 获取类的所有字段名称
     *
     * @param clazz 类类型
     * @return 字段名称列表
     */
    public static List<String> getFieldNames(Class<?> clazz) {
        AssertUtils.notNull(clazz, "argument clazz  must not null");
        return getMemberFields(clazz)
                .stream()
                .map(Field::getName)
                .collect(Collectors.toList());
    }

    /**
     * 获取成员变量
     *
     * @param clazz 类类型
     * @return 字段列表
     */
    private static List<Field> getMemberFields(Class<?> clazz) {
        return FIELDS.computeIfAbsent(clazz, WindReflectUtils::getClazzFields)
                .stream()
                // 过滤静态变量
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .collect(Collectors.toList());
    }

    private static List<Field> getClazzFields(Class<?> clazz) {
        if (clazz == null || clazz == Object.class) {
            return Collections.emptyList();
        }
        Field[] fields = clazz.getDeclaredFields();
        List<Field> result = new ArrayList<>(Arrays.asList(fields));
        result.addAll(getClazzFields(clazz.getSuperclass()));
        return result;
    }

    /**
     * 解析对象实现的接口上设置的泛型
     *
     * @param bean 对象
     * @return 接口上设置的泛型
     */
    public static Type[] resolveSuperInterfaceGenericType(@NotNull Object bean) {
        AssertUtils.notNull(bean, "argument bean must not null");
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        ResolvableType resolvableType = ResolvableType.forClass(targetClass);
        ResolvableType[] interfaces = resolvableType.getInterfaces();
        ResolvableType[] generics = interfaces[0].getGenerics();
        AssertUtils.notEmpty(generics, () -> targetClass.getName() + " 未设置泛型");
        return Arrays.stream(generics)
                .map(ResolvableType::getType)
                .toArray(Type[]::new);
    }
}
