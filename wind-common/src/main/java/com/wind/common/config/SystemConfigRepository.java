package com.wind.common.config;

import com.wind.common.exception.AssertUtils;
import com.wind.common.utils.StringJoinSplitUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * 应用系统配置 Repository
 *
 * @author wuxp
 * @date 2023-11-15 09:36
 **/
public interface SystemConfigRepository extends SystemConfigStorage {


    /**
     * 获取配置，如果配置为 null 抛出异常
     *
     * @param name 配置名称
     * @return 配置值
     */
    @NotNull
    default String requiredConfig(String name) {
        String result = getConfig(name);
        AssertUtils.notNull(result, () -> String.format("config %s not found", name));
        return result;
    }

    /**
     * 获取配置并转换
     *
     * @param name  配置名称
     * @param clazz 类类型
     * @return 配置值
     */
    @Nullable
    default <T> T getConfig(String name, Class<T> clazz) {
        return getConfig(name, clazz, null);
    }

    /**
     * 获取配置并转换
     *
     * @param name       配置名称
     * @param targetType 类类型
     * @return 配置值
     */
    @Nullable
    <T> T getConfig(String name, Class<T> targetType, T defaultValue);

    /**
     * 获取集合类型配置
     *
     * @param name       配置名称
     * @param targetType 类类型
     * @return 配置值
     */
    @NotEmpty <T> Set<T> getConfigs(String name, Class<T> targetType);

    /**
     * 获取集合类型配置
     *
     * @param name 配置名称
     * @return 配置值
     */
    @NotEmpty
    default Set<String> getConfigs(String name) {
        String config = getConfig(name);
        return StringJoinSplitUtils.split(config);
    }

    @Nullable
    default Boolean asBoolean(String name) {
        return getConfig(name, Boolean.TYPE);
    }

    @Nullable
    default Integer asInt(String name) {
        return getConfig(name, Integer.TYPE);
    }

    @Nullable
    default Long asLong(String name) {
        return getConfig(name, Long.TYPE);
    }

    /**
     * 获取 json 配置并转换
     *
     * @param name       配置名称
     * @param targetType 类类型
     * @return 配置值
     */
    @Nullable
    <T> T getJsonConfig(String name, Class<T> targetType);

    /**
     * 获取 json 配置并转换
     *
     * @param name       配置名称
     * @param targetType 类类型
     * @return 配置值
     */
    @Nullable
    <T> T getJsonConfig(String name, ParameterizedTypeReference<T> targetType);
}
