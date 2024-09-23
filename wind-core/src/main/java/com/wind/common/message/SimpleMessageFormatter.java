package com.wind.common.message;

import org.springframework.util.ObjectUtils;

/**
 * 解析占位符为：{} 格式的消息表达式
 * 例如：你好，我是：{}，来自 {}，今年 {} 岁， 很高兴认识 {}
 *
 * @author wuxp
 * @date 2023-10-30 07:47
 **/
public class SimpleMessageFormatter implements MessageFormatter {

    static final String DELIM_STR = "{}";

    @Override
    public String format(String pattern, Object... args) {
        if (ObjectUtils.isEmpty(args)) {
            return pattern;
        }

        StringBuilder result = new StringBuilder(pattern);
        for (int i = 0; i < args.length; i++) {
            int index = result.indexOf(DELIM_STR, i);
            if (index < 0) {
                continue;
            }
            result.replace(index, index + DELIM_STR.length(), String.valueOf(args[i]));
        }
        return result.toString();
    }
}
