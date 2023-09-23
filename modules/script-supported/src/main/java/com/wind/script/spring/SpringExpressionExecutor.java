package com.wind.script.spring;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.wind.common.WindConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author wuxp
 * @date 2023-09-23 10:07
 **/
@Slf4j
public final class SpringExpressionExecutor {

    private static final ParserContext PARSER_CONTEXT = new TemplateParserContext(WindConstants.DELIM_START, WindConstants.DELIM_END);

    private static final ExpressionParser PARSER = new SpelExpressionParser();

    /**
     * spring expression 缓存
     *
     * @key 表达式内容
     * @value 解析后的表达执行对象
     */
    private static final Cache<String, Expression> EXPRESSION_CACHES = Caffeine.newBuilder()
            // 设置最后一次写入或访问后经过固定时间过期
            .expireAfterWrite(24, TimeUnit.HOURS)
            // 初始的缓存空间大小
            .initialCapacity(500)
            // 缓存的最大条数
            .maximumSize(2000)
            .build();

    private SpringExpressionExecutor() {
        throw new AssertionError();
    }

    /**
     * @param expression        spring 表达式
     * @param evaluationContext 执行上下文
     * @return 执行结果
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> T eval(String expression, EvaluationContext evaluationContext) {
        return (T) eval(expression, evaluationContext, Object.class);
    }

    /**
     * 执行时忽略异常，返回 null
     *
     * @param expression        spring 表达式
     * @param evaluationContext 执行上下文
     * @return 执行结果
     */
    @Nullable
    public static <T> T evalIfErrorOfNullable(String expression, EvaluationContext evaluationContext) {
        try {
            return eval(expression, evaluationContext);
        } catch (Exception exception) {
            log.error("eval spring expression error, expression: {}，message: {}", exception, exception.getMessage());
            return null;
        }
    }

    /**
     * 执行 spring expression 表达式
     *
     * @param expression        spring 表达式
     * @param evaluationContext 执行上下文
     * @param desiredResultType 返回值类型
     * @return 执行结果
     */
    @Nullable
    public static <T> T eval(String expression, EvaluationContext evaluationContext, Class<T> desiredResultType) {
        if (StringUtils.hasLength(expression)) {
            return parseExpression(expression).getValue(evaluationContext, desiredResultType);
        }
        return null;
    }

    /**
     * @param expression spring 表达式
     * @param variables  执行上下文变量
     * @return 执行结果
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T eval(String expression, Map<String, Object> variables) {
        return (T) eval(expression, variables, Object.class);
    }

    /**
     * 执行时忽略异常，返回 null
     *
     * @param expression spring 表达式
     * @param variables  执行上下文变量
     * @return 执行结果
     */
    @Nullable
    public static <T> T evalIfErrorOfNullable(String expression, Map<String, Object> variables) {
        EvaluationContext evaluationContext = new StandardEvaluationContext();
        variables.forEach(evaluationContext::setVariable);
        return evalIfErrorOfNullable(expression, evaluationContext);
    }


    /**
     * 执行 spring expression 表达式
     *
     * @param expression        spring 表达式
     * @param variables         执行上下文变量
     * @param desiredResultType 返回值类型
     * @return 执行结果
     */
    @Nullable
    public static <T> T eval(String expression, Map<String, Object> variables, Class<T> desiredResultType) {
        EvaluationContext evaluationContext = new StandardEvaluationContext();
        variables.forEach(evaluationContext::setVariable);
        return eval(expression, evaluationContext, desiredResultType);
    }

    private static Expression parseExpression(String expression) {
        if (expression.contains("{#") && expression.contains(WindConstants.DELIM_END)) {
            // 模板字符串
            return EXPRESSION_CACHES.get(expression, (key) -> PARSER.parseExpression(key, PARSER_CONTEXT));
        } else {
            return EXPRESSION_CACHES.get(expression, PARSER::parseExpression);
        }
    }

}
