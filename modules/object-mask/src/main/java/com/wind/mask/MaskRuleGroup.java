package com.wind.mask;

import com.wind.mask.annotation.Sensitive;
import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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
 * @see MaskRule
 **/
@Data
public final class MaskRuleGroup {

    private static final String[] REGEX_PARTS = {"*", "$", "^", "+"};

    /**
     * 脱敏的目标类类型
     */
    @NotNull
    private final Class<?> target;

    /**
     * 字段的脱敏规则
     */
    private final Set<MaskRule> fieldRules;

    public MaskRuleGroup(Class<?> target, Collection<MaskRule> fieldRules) {
        this.target = target;
        this.fieldRules = new LinkedHashSet<>(fieldRules);
    }

    @Nullable
    public MaskRule matches(String fieldName) {
        if (fieldName == null && fieldRules.isEmpty()) {
            return null;
        }
        return fieldRules.stream()
                .filter(rule -> rule.matches(fieldName))
                .findFirst()
                .orElse(null);
    }

    /**
     * 将 {@link Map} 类型字段的 {@link MaskRule} 转为 {@link  MaskRuleGroup}
     *
     * @param rule Map 类型字段的规则
     */
    public static MaskRuleGroup convertMapFiledRule(MaskRule rule) {
        List<MaskRule> rules = rule.getKeys().stream()
                .map(key -> {
                    Pattern pattern = convertPattern(key);
                    return new MaskRule(rule.getFieldName(), Collections.emptyList(), rule.getSanitizer(), rule.getGlobalSanitizerType()) {
                        @Override
                        boolean matches(String name) {
                            return pattern.matcher(name).matches();
                        }
                    };
                })
                .collect(Collectors.toList());
        return new MaskRuleGroup(Map.class, rules);
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

    public static GroupBuilder builder() {
        return new GroupBuilder(Collections.emptyList());
    }

    /**
     * {@link MaskRuleGroup} 建造器
     */
    public static final class GroupBuilder {

        private final List<MaskRuleGroup> groups;

        public GroupBuilder(List<MaskRuleGroup> groups) {
            this.groups = new ArrayList<>(groups);
        }

        public RuleBuilder form(Class<?> target) {
            return new RuleBuilder(this, target);
        }
    }

    /**
     * {@link MaskRule} 建造器
     *
     * @see #mark(ObjectMasker, String...)
     */
    public static final class RuleBuilder {

        private final GroupBuilder builder;

        private final Class<?> clazz;

        private final List<MaskRule> fieldRules;

        public RuleBuilder(GroupBuilder builder, Class<?> clazz) {
            this.builder = builder;
            this.clazz = clazz;
            this.fieldRules = new ArrayList<>();
        }

        /**
         * 使用自定义的 {@link ObjectMasker} 实现
         *
         * @param sanitizer  脱敏器实例
         * @param fieldNames 需要脱敏的字段名称
         * @return RuleBuilder
         */
        public RuleBuilder mark(ObjectMasker<?, ?> sanitizer, String... fieldNames) {
            addRules(fieldNames, fieldName -> MaskRule.mark(fieldName, sanitizer));
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
        public RuleBuilder markMapFieldKeys(ObjectMasker<?, ?> sanitizer, Collection<String> fieldNames, String... keys) {
            addRules(fieldNames.toArray(new String[0]), fieldName -> MaskRule.mark(fieldName, sanitizer, keys));
            return this;
        }

        public RuleBuilder markMapFieldKeys(ObjectMasker<?, ?> sanitizer, String fieldName, String... keys) {
            markMapFieldKeys(sanitizer, Collections.singletonList(fieldName), keys);
            return this;
        }

        public RuleBuilder next(Class<?> target) {
            builder.groups.add(createRuleGroup());
            return new GroupBuilder(builder.groups).form(target);
        }

        public List<MaskRuleGroup> build() {
            builder.groups.add(createRuleGroup());
            return Collections.unmodifiableList(builder.groups);
        }

        public MaskRuleGroup last() {
            builder.groups.add(createRuleGroup());
            return build().get(builder.groups.size() - 1);
        }

        private void addRules(String[] names, Function<String, MaskRule> rueFactory) {
            fieldRules.addAll(Arrays.stream(names).map(rueFactory).collect(Collectors.toList()));
        }

        private MaskRuleGroup createRuleGroup() {
            fieldRules.addAll(parsesRules());
            return new MaskRuleGroup(clazz, fieldRules);
        }

        private List<MaskRule> parsesRules() {
            Predicate<String> fieldMatcher = buildClassFieldMatcher();
            List<MaskRule> result = new ArrayList<>();
            for (Field field : clazz.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                if (field.isAnnotationPresent(Sensitive.class)) {
                    result.add(MaskRule.mark(field.getName(), field.getAnnotation(Sensitive.class).sanitizer()));
                } else if (fieldMatcher.test(field.getName())) {
                    result.add(MaskRule.mark(field.getName(), clazz.getAnnotation(Sensitive.class).sanitizer()));
                }
            }
            return result;
        }

        private Predicate<String> buildClassFieldMatcher() {
            Sensitive sensitive = clazz.getAnnotation(Sensitive.class);
            List<MaskRule> maskRules = sensitive == null ? Collections.emptyList() : Arrays.stream(sensitive.names())
                    .map(name -> MaskRule.mark(name, sensitive.sanitizer()))
                    .collect(Collectors.toList());
            List<Pattern> patterns = maskRules.stream()
                    .map(rule -> convertPattern(rule.getFieldName()))
                    .collect(Collectors.toList());
            return fieldName -> patterns.stream().anyMatch(pattern -> pattern.matcher(fieldName).matches());
        }
    }
}
