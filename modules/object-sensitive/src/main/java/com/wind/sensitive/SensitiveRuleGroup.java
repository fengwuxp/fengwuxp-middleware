package com.wind.sensitive;

import com.wind.common.exception.AssertUtils;
import com.wind.sensitive.annotation.Sensitive;
import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 脱敏规则组
 *
 * @author wuxp
 * @date 2024-03-11 13:30
 **/
@Data
public final class SensitiveRuleGroup {

    private static final String[] REGEX_PARTS = {"*", "$", "^", "+"};

    /**
     * 脱敏的目标类
     */
    @NotNull
    private final Class<?> target;

    /**
     * 字段的脱敏规则
     */
    private final Set<SensitiveRule> fieldRules;

    public SensitiveRuleGroup(Class<?> target, Collection<SensitiveRule> fieldRules) {
        this.target = target;
        this.fieldRules = new LinkedHashSet<>(fieldRules);
    }

    @Nullable
    public SensitiveRule matches(String fieldName) {
        if (fieldName == null && fieldRules.isEmpty()) {
            return null;
        }
        return fieldRules.stream()
                .filter(rule -> rule.matches(fieldName))
                .findFirst()
                .orElse(null);
    }

    /**
     * 将 {@link Map} 类型字段的 {@link SensitiveRule} 转为 {@link  SensitiveRuleGroup}
     *
     * @param rule Map 类型字段的规则
     */
    public static SensitiveRuleGroup convertMapFiledRule(SensitiveRule rule) {
        List<SensitiveRule> rules = rule.getKeys().stream()
                .map(key -> {
                    Pattern pattern = convertPattern(key);
                    return new SensitiveRule(rule.fieldName, Collections.emptyList(), rule.sanitizer, rule.globalSanitizerType) {
                        @Override
                        boolean matches(String name) {
                            return pattern.matcher(name).matches();
                        }
                    };
                })
                .collect(Collectors.toList());
        return new SensitiveRuleGroup(Map.class, rules);
    }

    private static Pattern convertPattern(String value) {
        if (isRegex(value)) {
            return Pattern.compile(value, Pattern.CASE_INSENSITIVE);
        }
        return Pattern.compile(String.format(".*%s$", value), Pattern.CASE_INSENSITIVE);
    }

    private static boolean isRegex(String value) {
        return Arrays.stream(REGEX_PARTS).anyMatch(value::contains);
    }

    @Data
    public static class SensitiveRule {

        /**
         * 需要脱敏的字段名称
         */
        @NotNull
        private final String fieldName;

        /**
         * 在字段为 {@link java.util.Map} 类型的情况下用于保存需要脱敏的 keys
         * 支持正则表达式
         */
        @NotNull
        private final Set<String> keys;

        /**
         * 脱敏器
         */
        private final ObjectSanitizer<?> sanitizer;

        /**
         * 引用全局的 {@link ObjectSanitizer} 实现
         */
        @SuppressWarnings("rawtypes")
        private final Class<? extends ObjectSanitizer> globalSanitizerType;

        @SuppressWarnings("rawtypes")
        private SensitiveRule(String fieldName, Collection<String> keys, ObjectSanitizer<?> sanitizer,
                              Class<? extends ObjectSanitizer> globalSanitizerType) {
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
        public static SensitiveRule mark(String fieldName, ObjectSanitizer<?> sanitizer, String... keys) {
            AssertUtils.notNull(sanitizer, "argument sanitizer must not null");
            return new SensitiveRule(fieldName, keys == null ? Collections.emptyList() : Arrays.asList(keys), sanitizer, null);
        }

        /**
         * 创建一个全局脱敏规则
         *
         * @param fieldName           字段名称
         * @param globalSanitizerType 使用的全局脱敏类型
         */
        @SuppressWarnings("rawtypes")
        public static SensitiveRule mark(String fieldName, Class<? extends ObjectSanitizer> globalSanitizerType) {
            AssertUtils.notNull(globalSanitizerType, "argument globalSanitizerType must not null");
            return new SensitiveRule(fieldName, Collections.emptyList(), null, globalSanitizerType);
        }
    }

    public static GroupBuilder builder() {
        return new GroupBuilder(Collections.emptyList());
    }

    /**
     * {@link SensitiveRuleGroup} 建造器
     */
    public static final class GroupBuilder {

        private final List<SensitiveRuleGroup> groups;

        public GroupBuilder(List<SensitiveRuleGroup> groups) {
            this.groups = new ArrayList<>(groups);
        }

        public RuleBuilder form(Class<?> target) {
            return new RuleBuilder(this, target);
        }
    }

    /**
     * {@link SensitiveRule} 建造器
     *
     * @see #mark(Class, String...)
     * @see #mark(ObjectSanitizer, String...)
     */
    public static final class RuleBuilder {

        private final GroupBuilder builder;

        private final Class<?> clazz;

        private final List<SensitiveRule> fieldRules;

        public RuleBuilder(GroupBuilder builder, Class<?> clazz) {
            this.builder = builder;
            this.clazz = clazz;
            this.fieldRules = new ArrayList<>();
        }

        /**
         * 使用自定义的 {@link ObjectSanitizer} 实现
         *
         * @param sanitizer  脱敏器实例
         * @param fieldNames 需要脱敏的字段名称
         * @return RuleBuilder
         */
        public RuleBuilder mark(ObjectSanitizer<?> sanitizer, String... fieldNames) {
            addRules(fieldNames, fieldName -> SensitiveRule.mark(fieldName, sanitizer));
            return this;
        }

        /**
         * 标记 {@link java.util.Map} 类型的字段
         *
         * @param sanitizer  脱敏器实例
         * @param fieldNames 需要脱敏的字段名称
         * @param keys       需要脱敏的 Map keys
         * @return RuleBuilder
         */
        public RuleBuilder markMapFieldKeys(ObjectSanitizer<?> sanitizer, Collection<String> fieldNames, String... keys) {
            addRules(fieldNames.toArray(new String[0]), fieldName -> SensitiveRule.mark(fieldName, sanitizer, keys));
            return this;
        }

        public RuleBuilder markMapFieldKeys(ObjectSanitizer<?> sanitizer, String fieldName, String... keys) {
            markMapFieldKeys(sanitizer, Collections.singletonList(fieldName), keys);
            return this;
        }

        /**
         * 使用全局的 {@link ObjectSanitizer} 实现
         *
         * @param globalSanitizerType 脱敏器类类型
         * @param fieldNames          需要脱敏的字段名称
         * @return RuleBuilder
         */
        @SuppressWarnings("rawtypes")
        public RuleBuilder mark(Class<? extends ObjectSanitizer> globalSanitizerType, String... fieldNames) {
            addRules(fieldNames, fieldName -> SensitiveRule.mark(fieldName, globalSanitizerType));
            return this;
        }

        public RuleBuilder next(Class<?> target) {
            builder.groups.add(createRuleGroup());
            return new GroupBuilder(builder.groups).form(target);
        }

        public List<SensitiveRuleGroup> build() {
            builder.groups.add(createRuleGroup());
            return Collections.unmodifiableList(builder.groups);
        }

        public SensitiveRuleGroup last() {
            builder.groups.add(createRuleGroup());
            return build().get(builder.groups.size() - 1);
        }

        private void addRules(String[] names, Function<String, SensitiveRule> rueFactory) {
            fieldRules.addAll(Arrays.stream(names).map(rueFactory).collect(Collectors.toList()));
        }

        private SensitiveRuleGroup createRuleGroup() {
            fieldRules.addAll(parsesRules());
            return new SensitiveRuleGroup(clazz, fieldRules);
        }

        private List<SensitiveRule> parsesRules() {
            Predicate<String> fieldMatcher = buildClassFieldMatcher();
            List<SensitiveRule> result = new ArrayList<>();
            for (Field field : clazz.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                if (field.isAnnotationPresent(Sensitive.class)) {
                    result.add(SensitiveRule.mark(field.getName(), field.getAnnotation(Sensitive.class).sanitizer()));
                } else if (fieldMatcher.test(field.getName())) {
                    result.add(SensitiveRule.mark(field.getName(), clazz.getAnnotation(Sensitive.class).sanitizer()));
                }
            }
            return result;
        }

        private Predicate<String> buildClassFieldMatcher() {
            Sensitive sensitive = clazz.getAnnotation(Sensitive.class);
            List<SensitiveRule> sensitiveRules = sensitive == null ? Collections.emptyList() : Arrays.stream(sensitive.names())
                    .map(name -> SensitiveRule.mark(name, sensitive.sanitizer()))
                    .collect(Collectors.toList());
            List<Pattern> patterns = sensitiveRules.stream()
                    .map(rule -> convertPattern(rule.getFieldName()))
                    .collect(Collectors.toList());
            return fieldName -> patterns.stream().anyMatch(pattern -> pattern.matcher(fieldName).matches());
        }
    }
}
