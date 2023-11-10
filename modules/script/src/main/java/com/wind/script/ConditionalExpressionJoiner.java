package com.wind.script;

/**
 * @author wuxp
 * @date 2023-09-23 07:50
 **/
public interface ConditionalExpressionJoiner<T> {

    ConditionalExpressionJoiner<String> DEFAULT_JOINER = (left, right, op) -> String.format("%s %s %s", left, op.getOperator(), right);

    String join(T left, T right, ConditionalNode.Op op);
}
