package com.wind.logging.logback.mask;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.google.common.collect.ImmutableSet;
import com.wind.mask.MaskRuleRegistry;
import com.wind.mask.ObjectMaskPrinter;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 日志脱敏
 *
 * @author wuxp
 * @date 2024-08-07 15:47
 **/
public class MaskingMessageConverter extends ClassicConverter {

    public final static Set<Class<?>> IGNORE_CLASSES = new LinkedHashSet<>(ImmutableSet.of(
            Date.class
    ));

    public final static Set<String> IGNORE_PACKAGES = new LinkedHashSet<>();

    public final static MaskRuleRegistry LOG_MASK_RULE_REGISTRY = new MaskRuleRegistry();

    private final static ObjectMaskPrinter MASKER = new ObjectMaskPrinter(LOG_MASK_RULE_REGISTRY);

    static {
        IGNORE_PACKAGES.add("org.springframework.");
        IGNORE_PACKAGES.add("org.slf4j.");
        IGNORE_PACKAGES.add("org.apache.");
        IGNORE_PACKAGES.add("org.freemarker.");
        IGNORE_PACKAGES.add("org.hibernate.");
        IGNORE_PACKAGES.add("org.jetbrains.");
        IGNORE_PACKAGES.add("org.jodd.");
        IGNORE_PACKAGES.add("lombok.");
        IGNORE_PACKAGES.add("javax.persistence.");
        IGNORE_PACKAGES.add("java.net.");
        IGNORE_PACKAGES.add("javax.");
        IGNORE_PACKAGES.add("java.security.");
        IGNORE_PACKAGES.add("java.text.");
        IGNORE_PACKAGES.add("java.io.");
        IGNORE_PACKAGES.add("java.time.");
        IGNORE_PACKAGES.add("java.lang.reflect");
        IGNORE_PACKAGES.add("sun.");
        IGNORE_PACKAGES.add("com.google.");
        IGNORE_PACKAGES.add("com.alibaba.");
        IGNORE_PACKAGES.add("com.alipay.");
        IGNORE_PACKAGES.add("com.baidu.");
        IGNORE_PACKAGES.add("com.github.");
        IGNORE_PACKAGES.add("reactor.");
        IGNORE_PACKAGES.add("org.reactivestreams");
        IGNORE_PACKAGES.add("io.reactivex.");

    }

    @Override
    public String convert(ILoggingEvent event) {
        Object[] argumentArray = event.getArgumentArray();
        if (ObjectUtils.isEmpty(argumentArray)) {
            // TODO 字符串处理
            return event.getFormattedMessage();
        }
        try {
            Object[] args = Arrays.stream(argumentArray)
                    .map(object -> {
                        if (requireMask(object)) {
                            return MASKER.mask(object);
                        }
                        return object;
                    })
                    .toArray(Object[]::new);
            return MessageFormatter.arrayFormat(event.getMessage(), args).getMessage();
        } catch (Throwable throwable) {
            // TODO
            return event.getFormattedMessage();
        }
    }

    private boolean requireMask(Object o) {
        Class<?> useMaskClass = getUseMaskClass(o);
        return useMaskClass != null && LOG_MASK_RULE_REGISTRY.requireMask(useMaskClass);
    }

    @SuppressWarnings("unchecked")
    private Class<?> getUseMaskClass(Object o) {
        if (o == null) {
            return null;
        }
        if (isIgnoreMask(o) || o instanceof Throwable) {
            return null;
        }
        Class<?> clazz = o.getClass();
        if (clazz.isArray()) {
            return clazz.getComponentType();
        } else if (ClassUtils.isAssignable(Collection.class, clazz)) {
            Collection<Object> objects = (Collection<Object>) o;
            if (objects.isEmpty()) {
                return null;
            }
            return objects.iterator().next().getClass();
        }
        return clazz;
    }

    private boolean isIgnoreMask(Object o) {
        String name = o.getClass().getName();
        return IGNORE_CLASSES.stream().anyMatch(c -> c.isInstance(o) ||
                IGNORE_PACKAGES.stream().anyMatch(name::startsWith));
    }
}