package com.wind.transaction.core;

import com.wind.common.exception.AssertUtils;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * 交易上下文透传参数
 *
 * @author wuxp
 * @date 2023-12-18 11:34
 **/
public final class TransactionContextVariables {

    /**
     * 上下文变量
     */
    private final Map<String, Object> variables;

    private TransactionContextVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public static TransactionContextVariables of() {
        return new TransactionContextVariables(new HashMap<>());
    }

    /**
     * 通过浅拷贝的方式创建一个新的实例
     *
     * @param variables 变量
     * @return TransactionContextVariables 实例
     */
    public static TransactionContextVariables of(Map<String, Object> variables) {
        return new TransactionContextVariables(new HashMap<>(variables));
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T getVariable(String name) {
        return (T) variables.get(name);
    }

    @NotNull
    public <T> T requireVariable(String name) {
        T result = getVariable(name);
        AssertUtils.notNull(result, () -> "variable name =" + name + " must not null");
        return result;
    }

    public TransactionContextVariables put(String name, Object value) {
        variables.put(name, value);
        return this;
    }
}
