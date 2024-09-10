package com.wind.script.expression;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wuxp
 * @date 2024-09-10 14:31
 **/
@AllArgsConstructor
@Getter
public enum LogicalOp {

    /**
     * 并且运算
     */
    AND("&&", "并且"),

    /**
     * 或运算
     */
    OR("||", "或");

    private final String operator;

    private final String desc;
}
