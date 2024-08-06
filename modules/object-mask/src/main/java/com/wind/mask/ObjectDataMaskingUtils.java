package com.wind.mask;

import com.wind.common.exception.AssertUtils;
import com.wind.common.exception.BaseException;
import com.wind.common.exception.DefaultExceptionCode;
import com.wind.common.util.WindReflectUtils;
import com.wind.mask.annotation.Sensitive;
import com.wind.mask.masker.MaskerFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 数据脱敏，支持通过注解 {@link Sensitive} 或 手动注册 {@link ObjectDataMaskingUtils#registerRule(Class, String, Collection, Class)} 脱敏规则的方式
 *
 * @author wuxp
 * @date 2024-08-02 14:33
 **/
public final class ObjectDataMaskingUtils {

    private static final Map<Class<?>, Set<ObjectDesensitizationRule>> SENSITIVE_FIELDS = new ConcurrentHashMap<>();

    private ObjectDataMaskingUtils() {
        throw new AssertionError();
    }

    /**
     * 对象脱敏
     *
     * @param target 需要脱敏的对象
     * @return 脱敏后的对象
     */
    @Nullable
    public static <T> T sanitize(@Nullable T target) {
        if (target == null) {
            return null;
        }
        Class<?> clazz = target.getClass();
        if (requiredSanitize(clazz)) {
            Collection<ObjectDesensitizationRule> sensitiveFields = getSensitiveFields(clazz);
            for (ObjectDesensitizationRule rule : sensitiveFields) {
                try {
                    sanitizeField(rule, target);
                } catch (Exception exception) {
                    throw new BaseException(DefaultExceptionCode.COMMON_ERROR, "object sanitize error", exception);
                }
            }
        }
        return target;
    }

    /**
     * 是否需要脱敏
     *
     * @param clazz 类类型
     * @return true 需要
     */
    public static boolean requiredSanitize(Class<?> clazz) {
        return (clazz.isAnnotationPresent(Sensitive.class) && WindReflectUtils.findFields(clazz, Sensitive.class).length > 0) || !SENSITIVE_FIELDS.getOrDefault(clazz, Collections.emptySet()).isEmpty();
    }

    public static void registerRule(Class<?> target, String fieldName, Class<ObjectMasker<Object, Object>> sanitizer) {
        registerRule(target, fieldName, Collections.emptySet(), sanitizer);
    }

    public static void registerRule(Class<?> target, MaskRule rule) {
        registerRule(target, rule.getFieldName(), rule.getKeys(), rule.getSanitizer().getClass());
    }

    /**
     * 注册脱敏规则
     *
     * @param target    需要脱敏的类型
     * @param fieldName 需要脱敏的字段
     * @param keys      脱敏字中需要被脱敏的 key (嵌套对象或 json 字符串)
     * @param sanitizer 脱敏器类类型
     */
    @SuppressWarnings("rawtypes")
    public static void registerRule(@NotNull Class<?> target, @NotBlank String fieldName, @NotNull Collection<String> keys, @NotNull Class<? extends ObjectMasker> sanitizer) {
        AssertUtils.notNull(target, "argument target must not null");
        AssertUtils.hasText(fieldName, "argument fieldName must not empty");
        AssertUtils.notNull(keys, "argument fieldName must not null");
        AssertUtils.notNull(sanitizer, "argument sanitizer must not null");
        Set<ObjectDesensitizationRule> ruleMetas = SENSITIVE_FIELDS.computeIfAbsent(target, k -> new HashSet<>());
        ruleMetas.add(new ObjectDesensitizationRule(WindReflectUtils.findField(target, fieldName), keys.toArray(new String[0]), MaskerFactory.getObjectSanitizer(sanitizer)));
    }

    public static void clearClassRules(Class<?> target) {
        SENSITIVE_FIELDS.remove(target);
    }

    public static void clearClassRules(Class<?> target, Collection<String> fileNames) {
        SENSITIVE_FIELDS.compute(target, (key, rules) -> {
            if (rules == null) {
                return null;
            }
            return rules.stream().filter(ruleMeta -> !fileNames.contains(ruleMeta.field.getName())).collect(Collectors.toSet());
        });

    }

    public static void clearRules() {
        SENSITIVE_FIELDS.clear();
    }

    private static Collection<ObjectDesensitizationRule> getSensitiveFields(Class<?> clazz) {
        Set<ObjectDesensitizationRule> ruleMetas = SENSITIVE_FIELDS.getOrDefault(clazz, Collections.emptySet());
        return ruleMetas.isEmpty() ? findRulesOnAnnotation(clazz) : ruleMetas;
    }

    private static List<ObjectDesensitizationRule> findRulesOnAnnotation(Class<?> clazz) {
        return Arrays.stream(WindReflectUtils.findFields(clazz, Sensitive.class)).map(field -> {
            Sensitive annotation = field.getAnnotation(Sensitive.class);
            return new ObjectDesensitizationRule(field, annotation.names(), MaskerFactory.getObjectSanitizer(annotation.sanitizer()));
        }).collect(Collectors.toList());
    }


    private static void sanitizeField(ObjectDesensitizationRule rule, Object val) throws Exception {
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
