package com.wind.script;

import com.wind.script.expression.Operand;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;

/**
 * 条件节点
 *
 * @author wuxp
 * @date 2023-09-23 07:44
 **/
@Data
public class ConditionalNode {

    /**
     * 逻辑关系
     */
    public enum LogicalRelation {

        /**
         * 并且运算
         */
        AND,

        /**
         * 或运算
         */
        OR
    }

    @AllArgsConstructor
    @Getter
    public enum Op {

        /**
         * 等于
         */
        EQ("==", "等于"),

        /**
         * 不等于
         */
        NOT_EQ("!=", "不等于"),

        /**
         * 大于
         */
        GE(">", "大于"),

        /**
         * 小于
         */
        LE("<", "小于"),

        /**
         * 大于等于
         */
        GET(">=", "大于等于"),

        /**
         * 小于等于
         */
        LET("<=", "小于等于"),

        /**
         * xxx 为Null
         */
        IS_NULL("== null", "为Null"),

        /**
         * xxx 不为Null
         */
        NOT_NULL("!= null", "不为Null"),

        /**
         * 字符串、 集合、数组、Map 等类型包含操作
         */
        CONTAINS("contains", "包含"),

        NOT_CONTAINS("not contains", "包含"),

        /**
         * 数字范围操作，日期类型可以转换为时间戳处理
         */
        IN_RANG("in rang", "在范围...之内"),

        NOT_IN_RANG("not in rang", "不在范围...之内"),

        /**
         * 全局（root）方法
         */
        GLOBAL_METHOD("global", "全局方法");

        private final String operator;

        private final String desc;
    }

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
     * 逻辑运算关系 AND、OR
     */
    private LogicalRelation relation;

    /**
     * 子节点
     */
    private List<ConditionalNode> children;
}
