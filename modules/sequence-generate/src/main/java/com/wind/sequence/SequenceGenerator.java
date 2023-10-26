package com.wind.sequence;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * 序列号生成器
 *
 * @author wuxp
 * @date 2023-10-17 13:29
 **/
public interface SequenceGenerator {

    /**
     * 获取下一个序列号
     *
     * @return 序列号
     */
    String next();

    /**
     * 创建随机数字序列号
     *
     * @param len 序列号长度
     * @return 序列号
     */
    static String randomNumeric(int len) {
        return RandomStringUtils.randomNumeric(len);
    }

    /**
     * 创建随机数字序列号
     *
     * @param prefix 前缀
     * @param len    序列号长度
     * @return 序列号
     */
    static String randomNumeric(String prefix, int len) {
        return String.format("%s%s", prefix, randomNumeric(len));
    }

    /**
     * 创建随机数字字母序列号
     *
     * @param len 序列号长度
     * @return 序列号
     */
    static String randomAlphanumeric(int len) {
        return RandomStringUtils.randomAlphanumeric(len);
    }

    /**
     * 创建随机数字字母序列号
     *
     * @param prefix 前缀
     * @param len    序列号长度
     * @return 序列号
     */
    static String randomAlphanumeric(String prefix, int len) {
        return String.format("%s%s", prefix, randomAlphanumeric(len));
    }

    static SequenceGenerator randomNumericGenerator(String prefix, int len) {
        return () -> randomNumeric(prefix, len);
    }

    static SequenceGenerator randomAlphanumericGenerator(String prefix, int len) {
        return () -> randomAlphanumeric(prefix, len);
    }
}
