package com.wind.script.spring;

import com.google.common.collect.ImmutableSet;
import com.wind.common.exception.AssertUtils;
import com.wind.common.util.ServiceInfoUtils;
import com.wind.common.util.StringJoinSplitUtils;
import org.springframework.expression.spel.support.ReflectiveMethodResolver;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * 限制 spring 调用方法的范围
 *
 * @author wuxp
 * @date 2024-09-12 11:35
 **/
public final class WindSecurityReflectiveMethodResolver extends ReflectiveMethodResolver {

    private static final String ALL_PACKAGE_NAMES_PROPERTY_NAME = "spring.expression.allow.packages";

    private static final Set<String> DEFAULT_PACKAGES = ImmutableSet.of("java.lang.Class", "com.wind");

    private final Set<String> packages;

    public WindSecurityReflectiveMethodResolver() {
        this(loadPackages());
    }

    public WindSecurityReflectiveMethodResolver(Set<String> packages) {
        this(true, packages);
    }

    public WindSecurityReflectiveMethodResolver(boolean useDistance, Set<String> packages) {
        super(useDistance);
        this.packages = packages;
    }

    @Override
    protected Method[] getMethods(Class<?> type) {
        String name = type.getName();
        AssertUtils.isTrue(packages.stream().anyMatch(name::startsWith), () -> "不允许调用 class name = " + type.getName() + " 的方法");
        return super.getMethods(type);
    }

    private static Set<String> loadPackages() {
        String packages = ServiceInfoUtils.getSystemProperty(ALL_PACKAGE_NAMES_PROPERTY_NAME);
        if (packages == null) {
            return DEFAULT_PACKAGES;
        }
        Set<String> result = new HashSet<>(DEFAULT_PACKAGES);
        result.addAll(StringJoinSplitUtils.split(packages));
        return result;
    }
}
