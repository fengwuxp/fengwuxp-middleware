package com.wind.common.message;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MessageFormatterTest {

    @Test
    void testJavaFormat() {
        String result = MessageFormatter.java().format("你好，我是：{0}，来自 {1}，今年 {2} 岁， 很高兴认识{3}", "张三", "东北", 22, "在做的各位同学");
        Assertions.assertEquals("你好，我是：张三，来自 东北，今年 22 岁， 很高兴认识在做的各位同学", result);
    }

    @Test
    void testSimpleFormat() {
        String result = MessageFormatter.simple().format("你好，我是：{}，来自 {}，今年 {} 岁， 很高兴认识{}", "张三", "东北", 22, "在做的各位同学");
        Assertions.assertEquals("你好，我是：张三，来自 东北，今年 22 岁， 很高兴认识在做的各位同学", result);
    }
}