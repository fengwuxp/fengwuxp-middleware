package com.wind.mask.masker;

import com.wind.common.exception.AssertUtils;
import com.wind.mask.ObjectMasker;
import org.springframework.util.StringUtils;

import java.util.Collection;

/**
 * 基于内容范围的文本脱敏器
 *
 * @author wuxp
 * @date 2024-08-07 17:07
 **/
public class StringRangMasker implements ObjectMasker<String, String> {

    private final int begin;

    private final int end;

    public StringRangMasker(int begin, int end) {
        AssertUtils.isTrue(begin <= end, "argument begin must lte end");
        this.begin = begin;
        this.end = end;
    }

    public static StringRangMasker phone() {
        return new StringRangMasker(3, 7);
    }

    public static StringRangMasker secret() {
        return new StringRangMasker(-1, -1);
    }

    @Override
    public String mask(String text, Collection<String> keys) {
        if (StringUtils.hasText(text)) {
            int length = text.length();
            if (begin <= -1 || end <= -1) {
                // 全部脱敏
                return "******";
            }
            if (length <= begin || end > length) {
                // 不需要脱敏
                return text;
            }
            return maskText(text);
        }
        return text;
    }

    private String maskText(String input) {
        // 例：用 '*' 替换除了开头和结尾外的字符
        if (input.length() <= 2) {
            // 太短，不需要脱敏
            return input;
        }
        StringBuilder masked = new StringBuilder();
        masked.append(input, 0, begin);
        // 保留开头字符
        int distance = end - begin;
        for (int i = 0; i < distance; i++) {
            // TODO 待优化
            masked.append('*');
        }
        // 保留结尾字符
        masked.append(input, end, input.length());
        return masked.toString();
    }
}
