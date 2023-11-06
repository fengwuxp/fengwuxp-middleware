package com.wind.common.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息占位符描述
 *
 * @author wuxp
 * @date 2023-10-30 07:41
 **/
@Getter
public final class MessagePlaceholder {

    /**
     * 消息占位符表达式
     */
    private final String pattern;

    /**
     * 占位符参数
     */
    private final Object[] args;

    private MessagePlaceholder(String pattern, Object[] args) {
        this.pattern = pattern;
        this.args = args;
    }

    public static MessagePlaceholder of(String pattern, Object... args) {
        return new MessagePlaceholder(pattern, args == null ? new Object[0] : args);
    }
}
