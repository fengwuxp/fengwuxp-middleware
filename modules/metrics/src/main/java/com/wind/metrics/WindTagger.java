package com.wind.metrics;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * 数据打标者
 *
 * @author wuxp
 * @date 2024-09-10 10:04
 **/
public interface WindTagger {

    /**
     * 添加单个标签
     *
     * @param target 标记标签的数据对象
     * @param tag    标记的标签
     */
    void tagging(@NotNull Object target, @NotNull Tag tag);

    /**
     * 全量标记标签，将会计算数据原有标签和现有标签的差异，进行新增、更新、删除
     *
     * @param target       标记标签的数据对象
     * @param tagKeyValues the key/value pairs to add, elements mustn't be null
     */
    default void fullTagging(@NotNull Object target, String... tagKeyValues) {
        fullTagging(target, Tag.tags(tagKeyValues));
    }

    /**
     * 全量标记标签，将会计算数据原有标签和现有标签的差异，进行新增、更新、删除
     *
     * @param target 标记标签的数据对象
     * @param tags   标记的标签列表
     */
    void fullTagging(@NotNull Object target, @NotNull Collection<Tag> tags);

    /**
     * 是否支持处理
     *
     * @param rawType 业务数据类型
     * @return if true 支持
     */
    boolean supports(Class<?> rawType);

}
