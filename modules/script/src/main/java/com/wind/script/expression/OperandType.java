package com.wind.script.expression;

/**
 * 操作数类型
 *
 * @author wuxp
 * @date 2024-09-11 13:42
 **/
public enum OperandType {

    /**
     * 常量(字面量)
     */
    CONSTANT,

    /**
     * 变量
     */
    VARIABLE,

    /**
     * 表达式
     */
    EXPRESSION;
}
