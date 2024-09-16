package com.wind.mask;

import com.wind.common.WindConstants;
import com.wind.common.annotations.VisibleForTesting;
import com.wind.common.util.WindReflectUtils;
import com.wind.mask.annotation.Sensitive;
import com.wind.mask.masker.MaskerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.wind.mask.MaskRuleGroup.convertMapRules;

/**
 * 对象脱敏打印器
 *
 * @author wuxp
 * @date 2024-03-11 13:27
 **/
@Slf4j
public final class ObjectMaskPrinter implements ObjectMasker<Object, String> {

    /**
     * 对象 toString 时，连接字段的字符
     */
    private static final String FIELD_CONNECTOR = ", ";

    /**
     * 遍历对象，连接字符串时，最后需要移除的 {@link #FIELD_CONNECTOR} 长度
     */
    private static final int REMOVE_LENGTH = FIELD_CONNECTOR.length();

    /**
     * 对象不为空（null） toSting 后的最小长度
     */
    private static final int MIN_LENGTH = REMOVE_LENGTH + 1;

    private final MaskRuleRegistry rueRegistry;

    public ObjectMaskPrinter(MaskRuleRegistry rueRegistry) {
        this.rueRegistry = rueRegistry;
    }

    @Override
    public String mask(Object obj, Collection<String> keys) {
        try {
            IdentityLimitPrinter printer = new IdentityLimitPrinter();
            return printer.mask(obj);
        } catch (Throwable throwable) {
            log.warn("sanitize object error", throwable);
            return WindConstants.EMPTY;
        }
    }

    /**
     * 打印时通过 {@link #references} 检查对象是否存在循环引用的 ObjectSanitizer 实现
     * 通过 {@link #isOverPrintSize(int)} 限制打印数组的长度
     * 通过 {@link #maxPrintDepth} {@link #depthCounter} 限制递归打印对象的深度，避免超大对于 toString 占用过多的内存
     */
    @VisibleForTesting
    class IdentityLimitPrinter implements ObjectMasker<Object, String> {

        private static final String CYCLE_REF_FLAG = "@ref";

        /**
         * 允许打印最大的数组、集合 Map 对象的大小
         */
        @VisibleForTesting
        static final int MAX_COLLECTION_SIZE = 128;

        /**
         * 打印对象的引用缓存
         */
        private final List<Object> references = new ArrayList<>();

        /**
         * 最大打印深度
         */
        private final int maxPrintDepth;

        private final AtomicInteger depthCounter = new AtomicInteger();

        public IdentityLimitPrinter() {
            this(3);
        }

        public IdentityLimitPrinter(int maxPrintDepth) {
            this.maxPrintDepth = maxPrintDepth;
        }

        @Override
        public String mask(Object obj, Collection<String> keys) {
            return checkCycleRefAndSanitize(obj, null);
        }

        private String checkCycleRefAndSanitize(Object value, @Nullable MaskRule maskRule) {
            if (value == null) {
                return WindConstants.NULL;
            }
            if (isCycleRef(value)) {
                return String.format("%s[%s]", CYCLE_REF_FLAG, Integer.toHexString(hashCode()));
            }
            if (maskRule != null) {
                return printWithMaskRule(value, maskRule);
            }

            if (value instanceof String) {
                // TODO 单纯的字符串先不支持脱敏
                return (String) value;
            }
            if (value instanceof Throwable) {
                return value.toString();
            }
            Class<?> clazz = value.getClass();
            if (isLambdaExpression(clazz)) {
                return value.toString();
            }
            if (ClassUtils.isPrimitiveArray(clazz)) {
                // TODO 原始类型数组先不限制打印的长度
                return printPrimitiveArray(value);
            }
            if (noneSensitive(clazz)) {
                // 不需要脱敏的类型
                return String.valueOf(value);
            }
            if (depthCounter.incrementAndGet() > maxPrintDepth) {
                return String.format("%s 对象打印深度超过了：%d", value.getClass().getName(), maxPrintDepth);
            }
            try {
                return sanitizeByRule(value, maskRule);
            } finally {
                depthCounter.decrementAndGet();
            }
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        private String printWithMaskRule(Object value, MaskRule fieldRule) {
            WindMasker<Object, Object> masker = getMasker(fieldRule);
            if (masker == null) {
                return checkCycleRefAndSanitize(value, null);
            }
            Object result = masker instanceof ObjectMasker ? ((ObjectMasker) masker).mask(value, fieldRule.getKeys()) : masker.mask(value);
            return String.valueOf(result);
        }

        private boolean isCycleRef(Object value) {
            // 是否为循序引用（通过对象地址比较）
            boolean isCycleRef = references.stream().anyMatch(o -> o == value);
            if (!isCycleRef) {
                // 非循环引用，加入引用队列
                references.add(value);
            }
            return isCycleRef;
        }

        /**
         * @param value     脱敏的对象
         * @param fieldRule 在 {@param value} 为字段时，改字段的配置规则组
         */
        private String sanitizeByRule(Object value, @Nullable MaskRule fieldRule) {
            Class<?> clazz = value.getClass();
            if (clazz.isArray()) {
                return printArray((Object[]) value, fieldRule);
            }
            if (ClassUtils.isAssignable(Collection.class, clazz)) {
                // 先 copy 后转化为数组打印，避免 ConcurrentModificationException、UnsupportedOperationException 等异常
                Collection<?> objects = new ArrayList<>((Collection<?>) value);
                return printArray(objects.toArray(new Object[0]), fieldRule);
            }
            if (ClassUtils.isAssignable(Map.class, clazz)) {
                // TODO ConcurrentModificationException 异常处理（暂时没有发现，先不加复制处理）
                return printMap((Map<?, ?>) value, fieldRule);
            }
            return printObject(value);
        }

        private boolean noneSensitive(Class<?> clazz) {
            if (ClassUtils.isPrimitiveOrWrapper(clazz) || clazz.isEnum()) {
                return true;
            }
            return ClassUtils.isAssignable(Date.class, clazz) || ClassUtils.isAssignable(Temporal.class, clazz);
        }

        /**
         * copy form {@link Arrays#toString}
         */
        private String printArray(Object[] objects, MaskRule fieldRule) {
            int iMax = objects.length - 1;
            if (iMax == -1) {
                return "[]";
            }
            if (isOverPrintSize(objects.length)) {
                return toOverMaxSizeString(objects.getClass());
            }
            StringBuilder result = new StringBuilder();
            result.append('[');
            for (int i = 0; ; i++) {
                result.append(checkCycleRefAndSanitize(objects[i], fieldRule));
                if (i == iMax) {
                    return result.append(']').toString();
                }
                result.append(", ");
            }
        }

        private String printMap(Map<?, ?> map, MaskRule maskRule) {
            if (map.isEmpty()) {
                return "{}";
            }
            if (isOverPrintSize(map.size())) {
                return toOverMaxSizeString(map.getClass());
            }
            MaskRuleGroup group = maskRule == null ? rueRegistry.getRuleGroup(Map.class) : convertMapRules(maskRule);
            StringBuilder result = new StringBuilder();
            result.append('{');
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                Object key = entry.getKey();
                result.append(key).append("=");
                if (key instanceof String) {
                    MaskRule rule = group.matchesWithKey((String) key);
                    result.append(rule == null ? checkCycleRefAndSanitize(entry.getValue(), maskRule) : getMasker(rule).mask(entry.getValue()));
                } else {
                    result.append(mask(entry.getValue()));
                }
                result.append(", ");
            }
            deleteLastBlank(result);
            result.append('}');
            return result.toString();
        }

        private String printObject(Object obj) {
            Class<?> clazz = obj.getClass();
            StringBuilder result = new StringBuilder(obj.getClass().getSimpleName()).append("(");
            for (Field field : WindReflectUtils.getFields(clazz)) {
                ReflectionUtils.makeAccessible(field);
                Object value = ReflectionUtils.getField(field, obj);
                MaskRule rule = getFieldMaskRule(field);
                result.append(field.getName()).append("=").append(checkCycleRefAndSanitize(value, rule)).append(", ");
            }
            deleteLastBlank(result);
            result.append(')');
            return result.toString();
        }

        @Nullable
        private MaskRule getFieldMaskRule(Field field) {
            Sensitive annotation = field.getAnnotation(Sensitive.class);
            String name = field.getName();
            if (annotation == null) {
                MaskRuleGroup ruleGroup = rueRegistry.getRuleGroup(field.getDeclaringClass());
                return ruleGroup == null ? null : ruleGroup.matchesWithName(name);
            }
            return new MaskRule(name, Arrays.asList(annotation.names()), MaskerFactory.getMasker(annotation.masker()));
        }

        private String printPrimitiveArray(Object o) {
            if (o.getClass().getComponentType() == byte.class) {
                return Arrays.toString((byte[]) o);
            }
            if (o.getClass().getComponentType() == short.class) {
                return Arrays.toString((short[]) o);
            }
            if (o.getClass().getComponentType() == int.class) {
                return Arrays.toString((int[]) o);
            }
            if (o.getClass().getComponentType() == long.class) {
                return Arrays.toString((long[]) o);
            }
            if (o.getClass().getComponentType() == double.class) {
                return Arrays.toString((double[]) o);
            }
            if (o.getClass().getComponentType() == float.class) {
                return Arrays.toString((float[]) o);
            }
            if (o.getClass().getComponentType() == boolean.class) {
                return Arrays.toString((boolean[]) o);
            }
            if (o.getClass().getComponentType() == char.class) {
                return Arrays.toString((char[]) o);
            }
            return null;
        }

        @SuppressWarnings("unchecked")
        private WindMasker<Object, Object> getMasker(MaskRule rule) {
            if (rule == null) {
                return null;
            }
            return (WindMasker<Object, Object>) rule.getMasker();
        }

        private void deleteLastBlank(StringBuilder builder) {
            // 非常规写法，只是为了减少重复的代码
            if (builder.length() > MIN_LENGTH) {
                builder.deleteCharAt(builder.length() - REMOVE_LENGTH);
            }
        }

        private boolean isLambdaExpression(Class<?> clazz) {
            return clazz.getName().contains("$Lambda$");
        }

        private boolean isOverPrintSize(int size) {
            return size > MAX_COLLECTION_SIZE;
        }

        private String toOverMaxSizeString(Class<?> clazz) {
            return String.format("%s 对象的大小超过：%d", clazz.getName(), MAX_COLLECTION_SIZE);
        }
    }
}
