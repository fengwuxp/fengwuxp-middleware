package com.wind.metrics;

import com.wind.common.exception.AssertUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * @author wuxp
 * @date 2024-09-10 13:02
 **/
@Getter
@EqualsAndHashCode
@ToString
public final class Tag {

    /**
     * 标签名称，用于唯一标识一个 Tag
     */
    @NotNull
    private final String name;

    /**
     * 标签值
     */
    @NotNull
    private final String value;

    private Tag(@NotNull String name, @NotNull String value) {
        AssertUtils.hasText(name, "argument name must not emtpy");
        AssertUtils.hasText(value, "argument value must not emtpy");
        this.name = name;
        this.value = value;
    }

    public static Tag of(String name, String value) {
        return new Tag(name, value);
    }

    /**
     * Return a new {@code Tags} instance containing tags constructed from the specified
     * key/value pairs.
     *
     * @param keyValues the key/value pairs to add, elements mustn't be null
     */
    public static List<Tag> tags(String... keyValues) {
        Tag[] tags = new Tag[keyValues.length / 2];
        for (int i = 0; i < keyValues.length; i += 2) {
            tags[i / 2] = Tag.of(keyValues[i], keyValues[i + 1]);
        }
        return Arrays.asList(tags);
    }
}
