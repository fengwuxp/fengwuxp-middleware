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
     * 创建随机数字序列号生成器
     *
     * @param len 序列号长度
     * @return 序列号生成器
     */
    static SequenceGenerator randomNumeric(int len) {
        return () -> RandomStringUtils.randomNumeric(len);
    }

    /**
     * 创建随机数字字母序列号生成器
     *
     * @param len 序列号长度
     * @return 序列号生成器
     */
    static SequenceGenerator randomAlphanumeric(int len) {
        return () -> RandomStringUtils.randomNumeric(len);
    }

}
