package com.wind.script.spring;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.ImmutableMap;
import com.wind.common.exception.BaseException;
import com.wind.script.ConditionalNode;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.ResourceUtils;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wuxp
 * @date 2023-09-23 07:58
 **/
public class SpringExpressionGeneratorTests {

    private static final String EXPECTED_EXPRESSION = "#name == '张三' AND ({'dev','sit'}.contains(#env) AND ((#age >= 16 and #age <= 45) OR ({'杭州','上海'}.contains(#city) OR #tags['example'] != 'demo')))";

    private final ExpressionParser expressionParser = new SpelExpressionParser();

    @Test
    void generate() throws Exception {
        URI filepath = ResourceUtils.getURL("classpath:conditional-nodes.json").toURI();
        String json = IOUtils.toString(Files.newInputStream(Paths.get(filepath)), StandardCharsets.UTF_8.name());
        ConditionalNode conditionalNode = JSON.parseObject(json, ConditionalNode.class);
        String spel = SpringExpressionGenerator.generate(conditionalNode);
        Assertions.assertEquals(EXPECTED_EXPRESSION, spel);
        Expression expression = expressionParser.parseExpression(spel);
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("tags", ImmutableMap.of("example", "demo"));
        context.setVariable("city", "北京");
        context.setVariable("age", 26);
        context.setVariable("env", "dev");
        context.setVariable("name", "张三");
        Assertions.assertEquals(Boolean.TRUE, expression.getValue(context, Boolean.class));
    }

    @Test
    void generateOpByExpression() {
        ConditionalNode node = mockGlobalMethodNode(".success");
        String spel = SpringExpressionGenerator.generate(node);
        Assertions.assertEquals("#root.execCmd('test-cmd',#name,'张三',{'a':'b'}).success", spel);
    }

    @Test
    void generateOpByExpressionNoArgs() {
        ConditionalNode node = new ConditionalNode();
        node.setOp(ConditionalNode.Op.GLOBAL_METHOD);
        node.setLeft(new ConditionalNode.Operand("root.getExample", ConditionalNode.OperandSource.SCRIPT));
        node.setRight(new ConditionalNode.Operand("{}", ConditionalNode.OperandSource.SCRIPT));
        String spel = SpringExpressionGenerator.generate(node);
        Assertions.assertEquals("#root.getExample()", spel);
    }

    @Test()
    void generateOpByExpressionWithDisabled1() {
        ConditionalNode node = mockGlobalMethodNode("success == 1 and new xxx.File('/xxx').delete()");
        BaseException exception = Assertions.assertThrows(BaseException.class, () -> SpringExpressionGenerator.generate(node));
        Assertions.assertEquals("不允许使用：new 操作符", exception.getMessage());
    }


    @Test()
    void generateOpByExpressionWithDisabled2() {
        ConditionalNode node = mockGlobalMethodNode("success == 1 and T(java.lang.System.exit(0))");
        BaseException exception = Assertions.assertThrows(BaseException.class, () -> SpringExpressionGenerator.generate(node));
        Assertions.assertEquals("不允许使用：T( 操作符", exception.getMessage());
    }

    @Test
    void testSpringExpression() {
        Expression expression = expressionParser.parseExpression("{'java','Struts','Spring'}.contains('java')");
        Assertions.assertEquals(Boolean.TRUE, expression.getValue(Boolean.class));
        expression = expressionParser.parseExpression("{1,2,3}.contains(2)");
        Assertions.assertEquals(Boolean.TRUE, expression.getValue(Boolean.class));
        expression = expressionParser.parseExpression("{'a':'大学'}.containsKey('a')");
        Assertions.assertEquals(Boolean.TRUE, expression.getValue(Boolean.class));
    }

    @Test
    void testSpringExpressionWithNull() {
        Expression expression = expressionParser.parseExpression("{1,2,3}.contains(#test)");
        Assertions.assertEquals(Boolean.FALSE, expression.getValue(new StandardEvaluationContext(), Boolean.class));
    }

    private static ConditionalNode mockGlobalMethodNode(String expression) {
        ConditionalNode result = new ConditionalNode();
        result.setOp(ConditionalNode.Op.GLOBAL_METHOD);
        result.setLeft(new ConditionalNode.Operand("root.execCmd", ConditionalNode.OperandSource.SCRIPT));
        List<String> args = new ArrayList<>();
        args.add("#name");
        args.add("'张三'");
        args.add("{'a':'b'}");
        Map<String, String> config = ImmutableMap.of(
                "cmd", "test-cmd",
                "args", JSON.toJSONString(args),
                "result", expression
        );
        result.setRight(new ConditionalNode.Operand(JSON.toJSONString(config), ConditionalNode.OperandSource.SCRIPT));
        String jsonString = JSON.toJSONString(result);
        return result;
    }
}
