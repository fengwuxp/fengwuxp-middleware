package com.wind.script;

import com.wind.script.expression.LogicalOp;
import com.wind.script.expression.Op;
import com.wind.script.expression.Operand;
import lombok.Data;

import java.util.List;

/**
 * 条件表达式
 *
 * @author wuxp
 * @date 2023-09-23 07:44
 **/
@Data
public class ConditionalExpression {

    /**
     * 逻辑运算关系 AND、OR
     */
    private LogicalOp relation;

    /**
     * 左操作数
     */
    private Operand left;

    /**
     * 操作符
     */
    private Op op;

    /**
     * 右操作数
     */
    private Operand right;

    /**
     * 子节点
     */
    private List<ConditionalExpression> children;
}
