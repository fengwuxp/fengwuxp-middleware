package com.wind.script.expression;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 表达式操作数，{@link #getValue()}用于在表达式中计算
 *
 * @author wuxp
 * @date 2024-05-10 13:38
 **/
public interface Operand {

    /**
     * @return 操作数值
     */
    Object getValue();

    /**
     * @return 操作数来源
     */
    OperandSource getSource();

    /**
     * 生成一个常量操作数
     *
     * @param value 常量值
     * @return Operand 实例
     */
    static Operand ofConst(Object value) {
        return immutable(value, OperandSource.CONSTANT);
    }

    @JsonCreator()
    static Operand immutable(@JsonProperty("value") Object value, @JsonProperty("source") OperandSource source) {
        return new ImmutableOperand(value, source);
    }

    /**
     * 操作数来源
     */
    enum OperandSource {

        /**
         * 上下文变量
         */
        CONTEXT_VARIABLE,

        /**
         * 常量(字面量)
         */
        CONSTANT,

        /**
         * 脚本调用或执行
         */
        SCRIPT
    }

    @Getter
    @AllArgsConstructor
    class ImmutableOperand implements Operand {

        private final Object value;

        private final OperandSource source;
    }
}
