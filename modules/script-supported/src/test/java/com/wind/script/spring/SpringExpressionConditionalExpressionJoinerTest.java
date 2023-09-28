package com.wind.script.spring;

import com.wind.common.exception.BaseException;
import com.wind.script.ConditionalNode.Op;
import com.wind.script.ConditionalNode.Operand;
import com.wind.script.ConditionalNode.OperandSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class SpringExpressionConditionalExpressionJoinerTest {
    private final SpringExpressionConditionalExpressionJoiner joiner = new SpringExpressionConditionalExpressionJoiner();

    @Test
    public void testJoin() {
        Assertions.assertEquals("#user.age == 10", joiner.join(createContextVariable("user.age"), createConstant(10), Op.EQ));
        Assertions.assertEquals("#user.age > 10", joiner.join(createContextVariable("user.age"), createConstant(10), Op.GE));
        Assertions.assertEquals("#user.age >= 10", joiner.join(createContextVariable("user.age"), createConstant(10), Op.GET));
        Assertions.assertEquals("#user.age < 10", joiner.join(createContextVariable("user.age"), createConstant(10), Op.LE));
        Assertions.assertEquals("#user.age <= 10", joiner.join(createContextVariable("user.age"), createConstant(10), Op.LET));
        Assertions.assertEquals("(#user.age >= 1 and #user.age <= 100)",
                joiner.join(createContextVariable("user.age"), createConstant(new Integer[]{1, 100}), Op.IN_RANG));
        Assertions.assertEquals("(#user.age < 1 or #user.age > 100)",
                joiner.join(createContextVariable("user.age"), createConstant(new Integer[]{1, 100}), Op.NOT_IN_RANG));
        Assertions.assertEquals("#user.age.contains({1,100})",
                joiner.join(createContextVariable("user.age"), createConstant(Arrays.asList(1, 100)), Op.CONTAINS));
        Assertions.assertEquals("!#user.age.contains({1,100})",
                joiner.join(createContextVariable("user.age"), createConstant(Arrays.asList(1, 100)), Op.NOT_CONTAINS));
        Assertions.assertEquals("#user == null", joiner.join(createContextVariable("user"), null, Op.IS_NULL));
        Assertions.assertEquals("#user != null", joiner.join(createContextVariable("user"), null, Op.NOT_NULL));
        Assertions.assertEquals("#user.name == getUserName(#user))", joiner.join(createContextVariable("user.name"), createContextVariable("getUserName(#user))", OperandSource.SCRIPT), Op.EQ));
    }

    @Test()
     void testCallSpringBeanError() {
        BaseException exception = Assertions.assertThrows(BaseException.class, () -> joiner.join(createContextVariable("user.name"), createContextVariable("@example.getUserName(#user))", OperandSource.SCRIPT), Op.EQ));
        Assertions.assertEquals("不允许使用 @ 开头，访问 spring context bean 对象", exception.getMessage());
    }

    private static Operand createConstant(Object val) {
        return new Operand(val, OperandSource.CONSTANT);
    }

    private static Operand createContextVariable(String text) {
        return createContextVariable(text, OperandSource.CONTEXT_VARIABLE);
    }

    private static Operand createContextVariable(String text, OperandSource source) {
        return new Operand(text, source);
    }


}