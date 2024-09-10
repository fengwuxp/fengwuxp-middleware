package com.wind.script;

import com.wind.script.expression.Op;

/**
 * 条件表达式连接器
 *
 * @author wuxp
 * @date 2023-09-23 07:50
 **/
public interface ConditionalExpressionJoiner<T> {

    ConditionalExpressionJoiner<String> DEFAULT_JOINER = (left, right, op) -> String.format("%s %s %s", left, op.getOperator(), right);

    /**
     * 连接条件表达式
     *
     * @param left  左操作数
     * @param right 右操作数
     * @param op    操作符
     * @return 操作表达式
     */
    String join(T left, T right, Op op);
}
