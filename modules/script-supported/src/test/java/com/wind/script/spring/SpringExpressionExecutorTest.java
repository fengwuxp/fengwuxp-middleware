package com.wind.script.spring;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.HashMap;
import java.util.Map;

class SpringExpressionExecutorTest {


    @Test
    void testEval1() {
        Map<String, Object> evaluationContext = new HashMap<>();
        evaluationContext.put("name", "张三");
        evaluationContext.put("sex", "男");
        String text = SpringExpressionExecutor.eval("这是一个操作，操作人：{#name}，性别：{#sex}", evaluationContext);
        Assertions.assertEquals("这是一个操作，操作人：张三，性别：男", text);
    }

    @Test
    void testEval2() {
        EvaluationContext evaluationContext = new StandardEvaluationContext();
        evaluationContext.setVariable("id", "1");
        evaluationContext.setVariable("user", ImmutableMap.of("id", "2"));
        Assertions.assertEquals("1", SpringExpressionExecutor.eval("#id", evaluationContext));
        Assertions.assertEquals("2", SpringExpressionExecutor.eval("{#user['id']}", evaluationContext));
    }
}