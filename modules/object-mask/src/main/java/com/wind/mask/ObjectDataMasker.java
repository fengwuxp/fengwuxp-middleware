package com.wind.mask;

import com.wind.common.exception.AssertUtils;
import com.wind.common.exception.BaseException;
import com.wind.common.exception.DefaultExceptionCode;
import com.wind.common.util.WindDeepCopyUtils;
import com.wind.common.util.WindReflectUtils;
import com.wind.mask.annotation.Sensitive;
import com.wind.mask.masker.MaskerFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.lang.Nullable;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 数据对象脱敏器，支持通过注解 {@link Sensitive} 或 手动注册 {@link ObjectDataMasker#registerRule(Class, MaskRule)} 脱敏规则的方式
 *
 * @author wuxp
 * @date 2024-08-07 16:41
 **/
public class ObjectDataMasker {

    private final Map<Class<?>, Set<ObjectDataMasker.ObjectDesensitizationRule>> maskFields = new ConcurrentHashMap<>();

    /**
     * 使用源对象脱敏
     * 注意：该方法会改变源对象
     *
     * @param target 需要脱敏的对象
     * @return 脱敏后的对象
     */
    public <T> T mask(@Nullable T target) {
        return this.mask(target, o -> o);
    }

    /**
     * 使用原对象脱敏
     * 注意: 该方法会 deep copy 原对象，如果对象中某些数据类型的构造方法不可见将导致 copy 失败
     *
     * @param target 需要脱敏的对象
     * @return 脱敏后的对象
     */
    public <T> T maskWithDeepCopy(@Nullable T target) {
        return this.mask(target, WindDeepCopyUtils::copy);
    }

    /**
     * 对象脱敏
     *
     * @param target     需要脱敏的对象
     * @param deepCopyer 深拷贝函数
     * @return 脱敏后的对象
     */
    @Nullable
    public <T> T mask(@Nullable T target, Function<T, T> deepCopyer) {
        if (target == null) {
            return null;
        }
        T result = deepCopyer.apply(target);
        Class<?> clazz = target.getClass();
        if (requiredSanitize(clazz)) {
            Collection<ObjectDataMasker.ObjectDesensitizationRule> sensitiveFields = getSensitiveFields(clazz);
            for (ObjectDataMasker.ObjectDesensitizationRule rule : sensitiveFields) {
                try {
                    sanitizeField(rule, result);
                } catch (Exception exception) {
                    throw new BaseException(DefaultExceptionCode.COMMON_ERROR, "object sanitize error", exception);
                }
            }
        }
        return result;
    }

    /**
     * 是否需要脱敏
     *
     * @param clazz 类类型
     * @return true 需要
     */
    public boolean requiredSanitize(Class<?> clazz) {
        return (clazz.isAnnotationPresent(Sensitive.class) && WindReflectUtils.findFields(clazz, Sensitive.class).length > 0) || !maskFields.getOrDefault(clazz, Collections.emptySet()).isEmpty();
    }

    public void registerRule(Class<?> target, MaskRule rule) {
        AssertUtils.notNull(target, "argument target must not null");
        AssertUtils.notNull(rule, "argument rule must not empty");
        Set<ObjectDataMasker.ObjectDesensitizationRule> ruleMetas = maskFields.computeIfAbsent(target, k -> new HashSet<>());
        ruleMetas.add(new ObjectDataMasker.ObjectDesensitizationRule(WindReflectUtils.findField(target, rule.getFieldName()), rule.getKeys().toArray(new String[0]), rule.getSanitizer()));
    }

    public void clearRules(Class<?> target) {
        maskFields.remove(target);
    }

    public void clearRules(Class<?> target, Collection<String> fileNames) {
        maskFields.compute(target, (key, rules) -> {
            if (rules == null) {
                return null;
            }
            return rules.stream().filter(ruleMeta -> !fileNames.contains(ruleMeta.field.getName())).collect(Collectors.toSet());
        });

    }

    public void clearRules() {
        maskFields.clear();
    }

    private Collection<ObjectDataMasker.ObjectDesensitizationRule> getSensitiveFields(Class<?> clazz) {
        Set<ObjectDataMasker.ObjectDesensitizationRule> ruleMetas = maskFields.getOrDefault(clazz, Collections.emptySet());
        return ruleMetas.isEmpty() ? findRulesOnAnnotation(clazz) : ruleMetas;
    }

    private List<ObjectDataMasker.ObjectDesensitizationRule> findRulesOnAnnotation(Class<?> clazz) {
        return Arrays.stream(WindReflectUtils.findFields(clazz, Sensitive.class)).map(field -> {
            Sensitive annotation = field.getAnnotation(Sensitive.class);
            return new ObjectDataMasker.ObjectDesensitizationRule(field, annotation.names(), MaskerFactory.getObjectSanitizer(annotation.sanitizer()));
        }).collect(Collectors.toList());
    }


    private void sanitizeField(ObjectDataMasker.ObjectDesensitizationRule rule, Object val) throws Exception {
        Field field = rule.field;
        Object o = field.get(val);
        if (o == null) {
            return;
        }
        field.set(val, rule.sanitizer.mask(o, Arrays.asList(rule.names)));
    }


    @AllArgsConstructor
    @Getter
    private static class ObjectDesensitizationRule {

        private final Field field;

        private final String[] names;

        /**
         * 脱敏器
         */
        private final ObjectMasker<Object, Object> sanitizer;
    }
}
