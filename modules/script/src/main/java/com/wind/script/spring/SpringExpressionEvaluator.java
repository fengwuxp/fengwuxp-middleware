package com.wind.script.spring;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.wind.common.WindConstants;
import com.wind.common.exception.AssertUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.lang.Nullable;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 快捷执行 spring expression 支持
 * {@link #TEMPLATE} 模板字符串解析支持
 *
 * @author wuxp
 * @docs https://docs.spring.io/spring-framework/reference/core/expressions.html
 * @date 2023-09-23 10:07
 **/
@Slf4j
public final class SpringExpressionEvaluator {

    private static final ParserContext TEMPLATE_PARSER_CONTEXT = new TemplateParserContext(WindConstants.DELIM_START, WindConstants.DELIM_END);

    private static final ExpressionParser PARSER = new SpelExpressionParser();

    /**
     * spring expression 缓存
     *
     * @key 表达式内容
     * @value 解析后的表达执行对象
     */
    private static final Cache<String, Expression> EXPRESSION_CACHES = Caffeine.newBuilder()
            // 设置最后一次写入或访问后经过固定时间过期
            .expireAfterWrite(1, TimeUnit.DAYS)
            // 初始的缓存空间大小
            .initialCapacity(200)
            // 缓存的最大条数
            .maximumSize(2000)
            .build();

    public static final SpringExpressionEvaluator DEFAULT = new SpringExpressionEvaluator(null);

    /**
     * 解析包含 "{#xxx}" 格式的字符串
     *
     * @see <a href="https://docs.spring.io/spring-framework/reference/core/expressions/language-ref/templating.html">templating</a>
     */
    public static final SpringExpressionEvaluator TEMPLATE = new SpringExpressionEvaluator(TEMPLATE_PARSER_CONTEXT);

    private final ParserContext context;

    private SpringExpressionEvaluator(ParserContext context) {
        this.context = context;
    }

    /**
     * @param expression        spring 表达式
     * @param evaluationContext 执行上下文
     * @return 执行结果
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T eval(String expression, EvaluationContext evaluationContext) {
        return (T) eval(expression, evaluationContext, Object.class);
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
    public <T> T eval(String expression, EvaluationContext evaluationContext, Class<T> desiredResultType) {
        return parseExpression(expression).getValue(evaluationContext, desiredResultType);
    }

    /**
     * @param expression spring 表达式
     * @param variables  执行上下文变量
     * @return 执行结果
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T eval(String expression, Map<String, Object> variables) {
        return (T) eval(expression, variables, Object.class);
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
    public <T> T eval(String expression, Map<String, Object> variables, Class<T> desiredResultType) {
        return eval(expression, getEvaluationContext(variables), desiredResultType);
    }

    private Expression parseExpression(String expression) {
        AssertUtils.hasText(expression, "argument expression must not empty");
        if (context == null) {
            return EXPRESSION_CACHES.get(expression, PARSER::parseExpression);
        } else {
            // 模板字符串
            return EXPRESSION_CACHES.get(expression, key -> PARSER.parseExpression(key, context));
        }
    }

    @Nonnull
    private static EvaluationContext getEvaluationContext(Map<String, Object> variables) {
        EvaluationContext evaluationContext = new StandardEvaluationContext();
        variables.forEach(evaluationContext::setVariable);
        return evaluationContext;
    }
}
