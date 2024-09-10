package com.wind.script.spring;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.HashMap;
import java.util.Map;

class SpringExpressionEvaluatorTest {

    @Test
    void testEval1() {
        Map<String, Object> evaluationContext = new HashMap<>();
        evaluationContext.put("name", "张三");
        evaluationContext.put("sex", "男");
        String text = SpringExpressionEvaluator.TEMPLATE.eval("这是一个操作，操作人：{#name}，性别：{#sex}", evaluationContext);
        String expected = "这是一个操作，操作人：张三，性别：男";
        Assertions.assertEquals(expected, text);
        text = SpringExpressionEvaluator.TEMPLATE.eval(expected, evaluationContext);
        Assertions.assertEquals(expected, text);
    }

    @Test
    void testEval2() {
        EvaluationContext evaluationContext = new StandardEvaluationContext();
        evaluationContext.setVariable("id", "1");
        evaluationContext.setVariable("user", ImmutableMap.of("id", "2"));
        Assertions.assertEquals("1", SpringExpressionEvaluator.DEFAULT.eval("#id", evaluationContext));
        Assertions.assertEquals("2", SpringExpressionEvaluator.TEMPLATE.eval("{#user['id']}", evaluationContext));
    }

    @Test
    void testEval3() {
        ImmutableMap<String, Object> variables = ImmutableMap.of("id", "2");
        Assertions.assertEquals("2", SpringExpressionEvaluator.DEFAULT.eval("#id", variables));
        Assertions.assertNull(SpringExpressionEvaluator.TEMPLATE.eval("{#name}", variables));
    }
}