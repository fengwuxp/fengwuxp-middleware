package com.wind.mask;

import com.wind.common.WindConstants;
import com.wind.common.exception.AssertUtils;
import com.wind.mask.masker.MaskerFactory;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 脱敏规则
 *
 * @author wuxp
 * @date 2024-08-06 12:55
 **/
@Data
public class MaskRule {

    static final MaskRule EMPTY = new MaskRule(WindConstants.EMPTY, Collections.emptyList(), null, null);

    /**
     * 需要脱敏的字段名称
     */
    @NotNull
    private final String fieldName;

    /**
     * 在字段为 {@link java.util.Map} 类型或 json字符串等情况下用于保存需要脱敏的 keys
     * 支持正则表达式
     */
    @NotNull
    private final Set<String> keys;

    /**
     * 脱敏器
     */
    private final ObjectMasker<?, ?> sanitizer;

    /**
     * 引用全局的 {@link ObjectMasker} 实现
     */
    @SuppressWarnings("rawtypes")
    private final Class<? extends ObjectMasker> globalSanitizerType;

    @SuppressWarnings("rawtypes")
    public MaskRule(String fieldName, Collection<String> keys, ObjectMasker<?, ?> sanitizer,
                    Class<? extends ObjectMasker> globalSanitizerType) {
        this.fieldName = fieldName;
        this.keys = new HashSet<>(keys);
        this.sanitizer = sanitizer;
        this.globalSanitizerType = globalSanitizerType;
    }

    boolean matches(String name) {
        return Objects.equals(name, this.fieldName);
    }

    /**
     * 创建一个自定义脱敏规则
     *
     * @param fieldName 字段名称
     * @param sanitizer 自定义的脱敏器
     * @param keys      Map 类型字段的 Keys
     */
    public static MaskRule mark(String fieldName, ObjectMasker<?, ?> sanitizer, String... keys) {
        AssertUtils.notNull(sanitizer, "argument sanitizer must not null");
        return new MaskRule(fieldName, keys == null ? Collections.emptyList() : Arrays.asList(keys), sanitizer, null);
    }

    /**
     * 创建一个脱敏规则
     *
     * @param fieldName     字段名称
     * @param sanitizerType 使用脱敏类型
     */
    @SuppressWarnings("rawtypes")
    public static MaskRule mark(String fieldName, Class<? extends ObjectMasker> sanitizerType) {
        return mark(fieldName, Collections.emptySet(), sanitizerType);
    }


    /**
     * 创建一个脱敏规则
     *
     * @param fieldName     字段名称
     * @param keys          脱敏字中需要被脱敏的 key (嵌套对象或 json 字符串)
     * @param sanitizerType 使用脱敏类型
     */
    @SuppressWarnings("rawtypes")
    public static MaskRule mark(String fieldName, Collection<String> keys, Class<? extends ObjectMasker> sanitizerType) {
        AssertUtils.notNull(sanitizerType, "argument sanitizerType must not null");
        AssertUtils.notNull(keys, "argument keys must not null");
        return new MaskRule(fieldName, keys, MaskerFactory.getObjectSanitizer(sanitizerType), null);
    }


    /**
     * 创建一个全局脱敏规则
     *
     * @param fieldName           字段名称
     * @param globalSanitizerType 使用的全局脱敏类型
     */
    @SuppressWarnings("rawtypes")
    public static MaskRule markGlobal(String fieldName, Class<? extends ObjectMasker> globalSanitizerType) {
        AssertUtils.notNull(globalSanitizerType, "argument globalSanitizerType must not null");
        return new MaskRule(fieldName, Collections.emptyList(), null, globalSanitizerType);
    }
}