package com.wind.office.excel.export;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.format.Formatter;

import javax.annotation.Nullable;

/**
 * @author wuxp
 * @date 2024-01-04 09:43
 **/
@AllArgsConstructor
@Getter
public class ExcelWriteHead {

    /**
     * 列标题
     */
    private final String title;

    /**
     * 取值表达式
     * 默认使用 spring expression 表达式
     * {@link org.springframework.expression.spel.standard.SpelExpressionParser}
     */
    private final String expression;

    /**
     * 列转换器
     */
    @Nullable
    private final Formatter<?> formatter;

    public static ExcelWriteHead of(String title, String expression) {
        return of(title, expression, null);
    }

    public static ExcelWriteHead of(String title, String expression, Formatter<?> formatter) {
        return new ExcelWriteHead(title, expression, formatter);
    }
}
